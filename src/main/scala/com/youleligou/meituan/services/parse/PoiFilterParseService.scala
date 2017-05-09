package com.youleligou.meituan.services.parse

import com.google.inject.Inject
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.meituan.daos.{PoiDao, PoiSnapshotDao, PoiSnapshotDaoSearch}
import com.youleligou.meituan.modals.Poi
import play.api.libs.json._

import scala.concurrent.Future

class PoiFilterParseService @Inject()(poiSnapshotRepo: CassandraRepo[PoiSnapshotDao],
                                      poiRepo: CassandraRepo[PoiDao],
                                      poiSnapshotSearchRepo: ElasticSearchRepo[PoiSnapshotDaoSearch])
    extends com.youleligou.crawler.services.ParseService {

  final val Step: Int        = 1
  final val Precision: Float = 100F
  final val LatitudeKey      = "lat"
  final val LongitudeKey     = "lng"
  final val OffsetKey        = "page_index"
  final val LimitKey         = "apage"

  private def rounding(number: Float) = (Math.round(number * Precision) / Precision).toString

  private def getChildLinksByLocation(fetchResponse: FetchResponse): Seq[UrlInfo] = {
    val queryParameters   = fetchResponse.fetchRequest.urlInfo.queryParameters
    val bodyParameters    = fetchResponse.fetchRequest.urlInfo.bodyParameters
    val deep              = fetchResponse.fetchRequest.urlInfo.deep
    val originalLatitude  = queryParameters.getOrElse(LatitudeKey, "39").toFloat
    val originalLongitude = queryParameters.getOrElse(LongitudeKey, "116").toFloat

    for {
      latitudeSteps  <- -Step to Step if latitudeSteps != 0; latitudeDelta   = latitudeSteps / Precision
      longitudeSteps <- -Step to Step if longitudeSteps != 0; longitudeDelta = longitudeSteps / Precision
    } yield {
      val updatedQueryParameters = queryParameters + (LatitudeKey -> rounding(originalLatitude + latitudeDelta)) + (LongitudeKey -> rounding(
        originalLongitude + longitudeDelta))
      val updatedBodyParameters = bodyParameters + (OffsetKey -> "0")
      fetchResponse.fetchRequest.urlInfo.copy(queryParameters = updatedQueryParameters, bodyParameters = updatedBodyParameters, deep = deep + 1)
    }
  }

  private def getChildLinksByOffset(fetchResponse: FetchResponse): Seq[UrlInfo] = {
    val urlInfo = fetchResponse.fetchRequest.urlInfo
    val offset  = urlInfo.bodyParameters.getOrElse(OffsetKey, "0").toInt
    val limit   = urlInfo.bodyParameters.getOrElse(LimitKey, "1").toInt
    Seq(urlInfo.copy(bodyParameters = urlInfo.bodyParameters + (OffsetKey -> (offset + limit).toString)))
  }

  private def persist(pois: Seq[Poi]): Future[Any] = {
//    implicit converts
    val poiDaos: Seq[PoiDao]                              = pois
    val poiSnapshotDaos: Seq[PoiSnapshotDao]              = pois
    val poiSnapshotDaoSearches: Seq[PoiSnapshotDaoSearch] = poiSnapshotDaos

    //cassandra
    poiRepo.save(poiDaos)
    poiSnapshotRepo.save(poiSnapshotDaos)

    //es
    poiSnapshotSearchRepo.save(poiSnapshotDaoSearches)
    Future.successful(true)
  }

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val result = Json.parse(fetchResponse.content)

    result \ "msg" toOption match {
      case Some(JsString("成功")) =>
        val pois: Seq[Poi] = result \ "data" \ "poilist" toOption match {
          case Some(JsArray(poiJsValues)) =>
            poiJsValues.flatMap { poiJsValue =>
              poiJsValue.validate[Poi] match {
                case poi: JsSuccess[Poi] =>
                  Some(poi.value)
                case error: JsError =>
                  logger.warn("parse poi failed, {}", error.errors.toString())
                  None
              }
            }
          case _ =>
            logger.warn("parse poi failed, url {}", fetchResponse.fetchRequest.urlInfo)
            Seq.empty[Poi]
        }

        if (pois.nonEmpty)
          persist(pois)

      case _ =>
        logger.warn("parse poi failed, {}", result.toString())
    }

    val hasNextPage: Boolean = (result \ "data" \ "poi_has_next_page" toOption).contains(JsBoolean(true))
    ParseResult(
      fetchResponse = fetchResponse,
      childLink = if (!hasNextPage) getChildLinksByLocation(fetchResponse) else getChildLinksByOffset(fetchResponse)
    )

  }
}
