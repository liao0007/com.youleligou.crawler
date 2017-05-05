package com.youleligou.eleme.services.menu

import com.google.inject.Inject
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.eleme.daos.{CategoryDao, FoodSnapshotDao, FoodSnapshotSearch}
import com.youleligou.eleme.models.{Category, Restaurant}
import com.youleligou.eleme.repos.cassandra.RestaurantRepo
import play.api.libs.json._

import scala.util.control.NonFatal

class ParseService @Inject()(restaurantRepo: RestaurantRepo,
                             categoryRepo: CassandraRepo[CategoryDao],
                             foodSnapshotRepo: CassandraRepo[FoodSnapshotDao],
                             foodSnapshotSearchRepo: ElasticSearchRepo[FoodSnapshotSearch])
    extends com.youleligou.crawler.services.ParseService {

  private def persist(categories: Seq[Category], fetchResponse: FetchResponse) = {
    categoryRepo.save(categories)
    foodSnapshotRepo.save(categories.flatMap(_.foods))

    try {
      val pattern               = """.*restaurant_id=(\d*)""".r
      val pattern(restaurantId) = fetchResponse.fetchRequest.urlInfo.path

      restaurantRepo.rddFindById(restaurantId.toLong) map { restaurantDao =>
        implicit val restaurantModel: Restaurant = restaurantDao
        val foodSearches: Seq[FoodSnapshotSearch] = categories flatMap { implicit category =>
          val foods: Seq[FoodSnapshotSearch] = category.foods
          foods
        }
        foodSnapshotSearchRepo.save(foodSearches)
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
          item.validate[Category].asOpt
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
