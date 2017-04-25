package com.youleligou.eleme.services.restaurant

import com.google.inject.Inject
import com.outworkers.phantom.database.DatabaseProvider
import com.outworkers.phantom.dsl.ResultSet
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.eleme.daos.cassandra.{ElemeDatabase, RestaurantDao, RestaurantDefinitionDao}
import com.youleligou.eleme.models.Restaurant
import play.api.libs.json._

import scala.concurrent.Future

class ParseService @Inject()(val database: ElemeDatabase) extends com.youleligou.crawler.services.ParseService with DatabaseProvider[ElemeDatabase] {

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

  private def persist(restaurants: Seq[Restaurant]): Seq[Future[ResultSet]] = {
    val restaurantDaos: Seq[RestaurantDao] = restaurants
    val restaurantDefinitionDaos: Seq[RestaurantDefinitionDao] = restaurantDaos.map { restaurantDao =>
      RestaurantDefinitionDao(
        id = restaurantDao.id,
        address = restaurantDao.address,
        latitude = restaurantDao.latitude,
        longitude = restaurantDao.longitude,
        name = restaurantDao.name,
        licensesNumber = restaurantDao.licensesNumber,
        companyName = restaurantDao.companyName,
        createdAt = restaurantDao.createdAt
      )
    }

    database.restaurants.insertOrUpdate(restaurantDaos)
    database.restaurantDefinitions.insertOrUpdate(restaurantDefinitionDaos)
  }

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val restaurants = Json.parse(fetchResponse.content) match {
      case JsArray(restaurantsJsValue) =>
        restaurantsJsValue.flatMap { restaurant =>
          restaurant.validate[Restaurant] match {
            case restaurant: JsSuccess[Restaurant] =>
              Some(restaurant.value)
            case error: JsError =>
              logger.warn("parse restaurant failed, {}", error.errors.toString())
              None
          }
        }
      case _ =>
        logger.warn("parse restaurant failed, url {}", fetchResponse.fetchRequest.urlInfo.url)
        Seq.empty[Restaurant]
    }

    persist(restaurants)

    ParseResult(
      fetchResponse = fetchResponse,
      childLink = if (restaurants.isEmpty) getChildLinksByLocation(fetchResponse) else getChildLinksByOffset(fetchResponse)
    )
  }
}
