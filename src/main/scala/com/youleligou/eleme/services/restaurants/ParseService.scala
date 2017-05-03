package com.youleligou.eleme.services.restaurants

import com.google.inject.Inject
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.eleme.daos.{RestaurantDao, RestaurantSearchDao, RestaurantSnapshotDao}
import com.youleligou.eleme.models.{Restaurant, RestaurantSnapshot}
import play.api.libs.json._

import scala.concurrent.Future

class ParseService @Inject()(restaurantSnapshotRepo: CassandraRepo[RestaurantSnapshotDao],
                             restaurantRepo: CassandraRepo[RestaurantDao],
                             restaurantEsRepo: ElasticSearchRepo[RestaurantSearchDao])
    extends com.youleligou.crawler.services.ParseService {

  final val Step: Int        = 1
  final val Precision: Float = 100F
  final val LatitudeKey      = "latitude"
  final val LongitudeKey     = "longitude"
  final val OffsetKey        = "offset"
  final val LimitKey         = "limit"

  private def rounding(number: Float) = (Math.round(number * Precision) / Precision).toString

  private def getChildLinksByLocation(fetchResponse: FetchResponse): Seq[UrlInfo] = {
    val queryParameters   = fetchResponse.fetchRequest.urlInfo.queryParameters
    val deep              = fetchResponse.fetchRequest.urlInfo.deep
    val originalLatitude  = queryParameters.getOrElse(LatitudeKey, "39").toFloat
    val originalLongitude = queryParameters.getOrElse(LongitudeKey, "116").toFloat

    for {
      latitudeSteps  <- -Step to Step if latitudeSteps != 0; latitudeDelta   = latitudeSteps / Precision
      longitudeSteps <- -Step to Step if longitudeSteps != 0; longitudeDelta = longitudeSteps / Precision
    } yield {
      val updatedQueryParameters = queryParameters + (LatitudeKey -> rounding(originalLatitude + latitudeDelta)) + (LongitudeKey -> rounding(
        originalLongitude + longitudeDelta)) + (OffsetKey -> "0")
      fetchResponse.fetchRequest.urlInfo.copy(queryParameters = updatedQueryParameters, deep = deep + 1)
    }
  }

  private def getChildLinksByOffset(fetchResponse: FetchResponse): Seq[UrlInfo] = {
    val urlInfo = fetchResponse.fetchRequest.urlInfo
    val offset  = urlInfo.queryParameters.getOrElse(OffsetKey, "0").toInt
    val limit   = urlInfo.queryParameters.getOrElse(LimitKey, "30").toInt
    Seq(urlInfo.copy(queryParameters = urlInfo.queryParameters + (OffsetKey -> (offset + limit).toString)))
  }

  private def persist(restaurantSnapshots: Seq[RestaurantSnapshot]): Future[Any] = {
    val restaurants: Seq[Restaurant] = restaurantSnapshots map { restaurantSnapshot =>
      Restaurant(
        id = restaurantSnapshot.id,
        address = restaurantSnapshot.address,
        latitude = restaurantSnapshot.latitude,
        longitude = restaurantSnapshot.longitude,
        name = restaurantSnapshot.name,
        imagePath = restaurantSnapshot.imagePath,
        identification = restaurantSnapshot.identification
      )
    }

    val restaurantSnapshotDaos: Seq[RestaurantSnapshotDao] = restaurantSnapshots
    val restaurantDaos: Seq[RestaurantDao]                 = restaurants
    val restaurantSearchDaos: Seq[RestaurantSearchDao]     = restaurants

    //persist into cassandra
    restaurantSnapshotRepo.save(restaurantSnapshotDaos)
    restaurantRepo.save(restaurantDaos)

    //es
    restaurantEsRepo.save(restaurantSearchDaos)
  }

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val restaurants: Seq[RestaurantSnapshot] = Json.parse(fetchResponse.content) match {
      case JsArray(restaurantsJsValue) =>
        restaurantsJsValue.flatMap { restaurant =>
          restaurant.validate[RestaurantSnapshot] match {
            case restaurant: JsSuccess[RestaurantSnapshot] =>
              Some(restaurant.value)
            case error: JsError =>
              logger.warn("parse restaurant failed, {}", error.errors.toString())
              None
          }
        }
      case _ =>
        logger.warn("parse restaurant failed, url {}", fetchResponse.fetchRequest.urlInfo.url)
        Seq.empty[RestaurantSnapshot]
    }

    persist(restaurants)

    ParseResult(
      fetchResponse = fetchResponse,
      childLink = if (restaurants.isEmpty) getChildLinksByLocation(fetchResponse) else getChildLinksByOffset(fetchResponse)
    )
  }
}
