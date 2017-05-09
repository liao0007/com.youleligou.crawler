package com.youleligou.meituan.services.parse

import com.google.inject.Inject
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.meituan.daos._
import com.youleligou.meituan.modals.FoodTag
import com.youleligou.meituan.repos.cassandra.PoiRepo
import play.api.libs.json._

import scala.util.control.NonFatal

class PoiFoodParseService @Inject()(restaurantRepo: PoiRepo,
                                    categoryRepo: CassandraRepo[FoodTagDao],
                                    categorySnapshotRepo: CassandraRepo[FoodTagSnapshotDao],
                                    spuSnapshotRepo: CassandraRepo[SpuSnapshotDao],
                                    skuSnapshotRepo: CassandraRepo[SkuSnapshotDao],
                                    spuSnapshotSearchRepo: ElasticSearchRepo[SpuSnapshotDaoSearch])
    extends com.youleligou.crawler.services.ParseService {

  private def persist(categories: Seq[FoodTag], fetchResponse: FetchResponse) =
    try {
      val wmPoiId = fetchResponse.fetchRequest.urlInfo.bodyParameters("wm_poi_id").toLong

      restaurantRepo.findByWmPoiViewId(wmPoiId) foreach { implicit restaurantDao =>
        categoryRepo.save(categories)
        categorySnapshotRepo.save(categories)
        spuSnapshotRepo.save(categories.flatMap(_.spus))
        skuSnapshotRepo.save(categories.flatMap(_.spus).flatMap(_.skus))

        implicit val restaurantDaoSearch: PoiDaoSearch = restaurantDao

        val foodSnapshotDaoSearches: Seq[SpuSnapshotDaoSearch] = categories flatMap { category =>
          val categoryDao: FoodTagDao                      = category
          implicit val categoryDaoSearch: FoodTagDaoSearch = categoryDao

          category.spus map { food =>
            implicit val foodSkus                           = food.skus
            val foodSnapshotDao: SpuSnapshotDao             = food
            val foodSnapshotDaoSearch: SpuSnapshotDaoSearch = foodSnapshotDao
            foodSnapshotDaoSearch
          }
        }
        spuSnapshotSearchRepo.save(foodSnapshotDaoSearches)
      }
    } catch {
      case NonFatal(x) =>
        logger.warn("{} {}", this.getClass, x.getMessage)
    }

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val categories: Seq[FoodTag] = Json.parse(fetchResponse.content) match {
      case JsArray(value) =>
        value flatMap { item =>
          item
            .validate[FoodTag]
            .fold({ reason =>
              logger.warn("parse menu failed, {}", reason.toString)
              None
            }, { category =>
              Some(category)
            })
        }
      case _ => Seq.empty[FoodTag]
    }

    persist(categories, fetchResponse)

    ParseResult(
      fetchResponse = fetchResponse,
      childLink = Seq.empty[UrlInfo]
    )
  }
}
