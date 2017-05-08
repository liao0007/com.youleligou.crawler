package com.youleligou.eleme.services.parse

import com.google.inject.Inject
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.eleme.daos.{RestaurantDaoSearch, _}
import com.youleligou.eleme.models.Category
import com.youleligou.eleme.repos.cassandra.RestaurantRepo
import play.api.libs.json._

import scala.util.control.NonFatal

class MenuParseService @Inject()(restaurantRepo: RestaurantRepo,
                                 categoryRepo: CassandraRepo[CategoryDao],
                                 categorySnapshotRepo: CassandraRepo[CategorySnapshotDao],
                                 foodSnapshotRepo: CassandraRepo[FoodSnapshotDao],
                                 foodSkuSnapshotRepo: CassandraRepo[FoodSkuSnapshotDao],
                                 foodSnapshotSearchRepo: ElasticSearchRepo[FoodSnapshotDaoSearch])
    extends com.youleligou.crawler.services.ParseService {

  private def persist(categories: Seq[Category], fetchResponse: FetchResponse) =
    try {
      val pattern               = """.*restaurant_id=(\d*)""".r
      val pattern(restaurantId) = fetchResponse.fetchRequest.urlInfo.path

      restaurantRepo.findById(restaurantId.toLong) foreach { implicit restaurantDao =>
        categoryRepo.save(categories)
        categorySnapshotRepo.save(categories)
        foodSnapshotRepo.save(categories.flatMap(_.foods))
        foodSkuSnapshotRepo.save(categories.flatMap(_.foods).flatMap(_.specFoods))

        implicit val restaurantDaoSearch: RestaurantDaoSearch = restaurantDao

        val foodSnapshotDaoSearches: Seq[FoodSnapshotDaoSearch] = categories flatMap { category =>
          val categoryDao: CategoryDao                      = category
          implicit val categoryDaoSearch: CategoryDaoSearch = categoryDao

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
