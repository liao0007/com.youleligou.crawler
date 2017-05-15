package com.youleligou.baidu.services.parse

import com.google.inject.Inject
import com.youleligou.baidu.daos._
import com.youleligou.baidu.models.Shop
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import play.api.libs.json._

import scala.concurrent.Future

class ShopParseService @Inject()(shopSnapshotRepo: CassandraRepo[ShopSnapshotDao],
                                 shopRepo: CassandraRepo[ShopDao],
                                 shopSnapshotSearchRepo: ElasticSearchRepo[ShopSnapshotDaoSearch])
    extends com.youleligou.crawler.services.ParseService {

  final val Step: Int        = 1
  final val Precision: Float = 100F
  final val LatitudeKey      = "lat"
  final val LongitudeKey     = "lng"
  final val OffsetKey        = "page"
  final val LimitKey         = "count"

  private def rounding(number: Float) = (Math.round(number * Precision) / Precision).toString

  private def getChildLinksByLocation(fetchResponse: FetchResponse): Seq[UrlInfo] = {
    val queryParameters: Map[String, String] = fetchResponse.fetchRequest.urlInfo.queryParameters
    val bodyParameters: Map[String, String]  = fetchResponse.fetchRequest.urlInfo.bodyParameters
    val deep: Int                            = fetchResponse.fetchRequest.urlInfo.deep
    val originalLatitude: Float              = queryParameters.getOrElse(LatitudeKey, "39").toFloat
    val originalLongitude: Float             = queryParameters.getOrElse(LongitudeKey, "116").toFloat

    for {
      latitudeSteps  <- -Step to Step if latitudeSteps != 0; latitudeDelta   = latitudeSteps / Precision * 100000  //baidu * 10e5
      longitudeSteps <- -Step to Step if longitudeSteps != 0; longitudeDelta = longitudeSteps / Precision * 100000 //baidu * 10e5
    } yield {
      val updatedQueryParameters = queryParameters + (LatitudeKey -> rounding(originalLatitude + latitudeDelta)) + (LongitudeKey -> rounding(
        originalLongitude + longitudeDelta)) + (OffsetKey -> "1")
      fetchResponse.fetchRequest.urlInfo.copy(queryParameters = updatedQueryParameters, deep = deep + 1)
    }
  }

  private def getChildLinksByOffset(fetchResponse: FetchResponse): Seq[UrlInfo] = {
    val urlInfo = fetchResponse.fetchRequest.urlInfo
    val offset  = urlInfo.queryParameters.getOrElse(OffsetKey, "1").toInt
    val limit   = urlInfo.queryParameters.getOrElse(LimitKey, "20").toInt
    Seq(urlInfo.copy(queryParameters = urlInfo.queryParameters + (OffsetKey -> (offset + limit).toString)))
  }

  private def persist(shops: Seq[Shop]): Future[Any] = {
//    implicit converts
    val shopDaos: Seq[ShopDao]                              = shops
    val shopSnapshotDaos: Seq[ShopSnapshotDao]              = shops
    val shopSnapshotDaoSearches: Seq[ShopSnapshotDaoSearch] = shopSnapshotDaos

    //cassandra
    shopRepo.save(shopDaos)
    shopSnapshotRepo.save(shopSnapshotDaos)

    //es
    shopSnapshotSearchRepo.save(shopSnapshotDaoSearches)
    Future.successful(true)
  }

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val result = Json.parse(fetchResponse.content)

    val shops: Seq[Shop] = result \ "error_msg" toOption match {
      case Some(JsString("")) =>
        val shops: Seq[Shop] = result \ "result" \ "shop_info" toOption match {
          case Some(JsArray(shopJsValues)) =>
            shopJsValues.flatMap { shopJsValue =>
              shopJsValue.validate[Shop] match {
                case shop: JsSuccess[Shop] =>
                  Some(shop.value)
                case error: JsError =>
                  logger.warn("parse shop failed, {}", error.errors.toString())
                  None
              }
            }
          case _ =>
            logger.warn("parse shop failed, url {}", fetchResponse.fetchRequest.urlInfo)
            Seq.empty[Shop]
        }

        if (shops.nonEmpty)
          persist(shops)
        shops

      case _ =>
        logger.warn("parse shop failed, {}", result.toString())
        Seq.empty[Shop]
    }

    val hasNextPage: Boolean = shops.nonEmpty
    ParseResult(
      fetchResponse = fetchResponse,
      childLink = if (!hasNextPage) getChildLinksByLocation(fetchResponse) else getChildLinksByOffset(fetchResponse)
    )

  }
}
