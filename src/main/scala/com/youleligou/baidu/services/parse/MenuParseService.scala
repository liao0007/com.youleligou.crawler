package com.youleligou.baidu.services.parse

import com.google.inject.Inject
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.meituan.daos._
import com.youleligou.meituan.modals.FoodTag
import com.youleligou.meituan.repos.cassandra.PoiRepo
import play.api.libs.json._

import scala.util.control.NonFatal

class MenuParseService @Inject()(restaurantRepo: PoiRepo,
                                 categoryRepo: CassandraRepo[FoodTagDao],
                                 categorySnapshotRepo: CassandraRepo[FoodTagSnapshotDao],
                                 spuSnapshotRepo: CassandraRepo[SpuSnapshotDao],
                                 skuSnapshotRepo: CassandraRepo[SkuSnapshotDao],
                                 spuSnapshotSearchRepo: ElasticSearchRepo[SpuSnapshotDaoSearch])
    extends com.youleligou.crawler.services.ParseService {

  private def persist(foodTags: Seq[FoodTag], fetchResponse: FetchResponse) =
    try {
      val wmPoiId = fetchResponse.fetchRequest.urlInfo.bodyParameters("wm_poi_id").toLong

      restaurantRepo.findByWmPoiViewId(wmPoiId) foreach { implicit restaurantDao =>
        categoryRepo.save(foodTags)
        categorySnapshotRepo.save(foodTags)

        spuSnapshotRepo.save(foodTags flatMap { foodTag =>
          implicit val foodTagDao: FoodTagDao = foodTag
          implicitly[Seq[SpuSnapshotDao]](foodTag.spus)
        })

        skuSnapshotRepo.save(foodTags.flatMap { foodTag =>
          foodTag.spus.map(spu => spu -> foodTag)
        } flatMap {
          case (spu, foodTag) =>
            implicit val foodTagDao: FoodTagDao         = foodTag
            implicit val spuSnapshotDao: SpuSnapshotDao = spu
            implicitly[Seq[SkuSnapshotDao]](spu.skus)
        })

        implicit val restaurantDaoSearch: PoiDaoSearch = restaurantDao

        val foodSnapshotDaoSearches: Seq[SpuSnapshotDaoSearch] = foodTags flatMap { category =>
          implicit val categoryDao: FoodTagDao             = category
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
    val result = Json.parse(fetchResponse.content)

    result \ "msg" toOption match {
      case Some(JsString("成功")) =>
        val foodSpuTags: Seq[FoodTag] = result \ "data" \ "food_spu_tags" toOption match {
          case Some(JsArray(foodSpuTagValues)) =>
            foodSpuTagValues.flatMap { foodSpuTagValue =>
              foodSpuTagValue.validate[FoodTag] match {
                case foodTag: JsSuccess[FoodTag] =>
                  Some(foodTag.value)
                case error: JsError =>
                  logger.warn("parse food spu tags failed, {}", error.errors.toString())
                  None
              }
            }
          case _ =>
            logger.warn("parse food spu tags failed, url {}", fetchResponse.fetchRequest.urlInfo)
            Seq.empty[FoodTag]
        }

        if (foodSpuTags.nonEmpty)
          persist(foodSpuTags, fetchResponse)

      case _ =>
        logger.warn("parse food spu tags failed, {}", result.toString())
    }

    ParseResult(
      fetchResponse = fetchResponse,
      childLink = Seq.empty[UrlInfo]
    )
  }
}
