package com.youleligou.eleme.services.menu

import com.google.inject.Inject
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.eleme.daos.{CategoryDao, FoodSkuSnapshotDao, FoodSnapshotDao, FoodSnapshotDaoSearch}
import com.youleligou.eleme.models.{Category, Restaurant}
import com.youleligou.eleme.repos.cassandra.RestaurantRepo
import play.api.libs.json._

import scala.util.control.NonFatal

class ParseService @Inject()(restaurantRepo: RestaurantRepo,
                             categoryRepo: CassandraRepo[CategoryDao],
                             foodSnapshotRepo: CassandraRepo[FoodSnapshotDao],
                             foodSkuSnapshotRepo: CassandraRepo[FoodSkuSnapshotDao],
                             foodSnapshotSearchRepo: ElasticSearchRepo[FoodSnapshotDaoSearch])
    extends com.youleligou.crawler.services.ParseService {

  private def persist(categories: Seq[Category], fetchResponse: FetchResponse) = {
    /*
    cassandra
     */
    categoryRepo.save(categories)
    foodSnapshotRepo.save(categories.flatMap(_.foods))
    foodSkuSnapshotRepo.save(categories.flatMap(_.foods).flatMap(_.specFoods))

    /*
    es
     */
    try {
      val pattern               = """.*restaurant_id=(\d*)""".r
      val pattern(restaurantId) = fetchResponse.fetchRequest.urlInfo.path

      restaurantRepo.findById(restaurantId.toLong) foreach { restaurantDao =>
        implicit val restaurantModel: Restaurant = restaurantDao

        val foodSnapshotDaoSearches: Seq[FoodSnapshotDaoSearch] = categories flatMap { implicit category =>
          category.foods map { food =>
            implicit val foodSkus                            = food.specFoods
            val foodSnapshotDao: FoodSnapshotDao             = food
            val foodSnapshotDaoSearch: FoodSnapshotDaoSearch = foodSnapshotDao
            foodSnapshotDaoSearch
          }
        }
        foodSnapshotSearchRepo.save(foodSnapshotDaoSearches)
      }
    } catch {
      case NonFatal(x) =>
        logger.warn("{} {}", this.getClass, x.getMessage)
    }

  }

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val categories: Seq[Category] = Json.parse(fetchResponse.content) match {
      case JsArray(value) =>
        value flatMap { item =>
          item
            .validate[Category]
            .fold({ reason =>
              logger.warn("parse menu failed, {}", reason.toString)
              None
            }, { category =>
              Some(category)
            })
        }
      case _ => Seq.empty[Category]
    }

    persist(categories, fetchResponse)

    ParseResult(
      fetchResponse = fetchResponse,
      childLink = Seq.empty[UrlInfo]
    )
  }
}
