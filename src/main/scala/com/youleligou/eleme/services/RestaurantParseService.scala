package com.youleligou.eleme.services

import com.google.inject.Inject
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.crawler.services.ParseService
import com.youleligou.eleme.daos.{Restaurant, RestaurantRepo}
import play.api.libs.json._

import scala.concurrent.Future
import scala.util.Try

class RestaurantParseService @Inject()(restaurantRepo: RestaurantRepo) extends ParseService {

  final val Length: Int      = 2
  final val Precision: Float = 10F
  final val LatitudeKey      = "latitude"
  final val LongitudeKey     = "longitude"
  final val OffsetKey        = "offset"

  private def getChildLinksByLocation(fetchResponse: FetchResponse): Seq[UrlInfo] = {
    val UrlInfo(domain, path, queryParameters, urlType, deep) = fetchResponse.fetchRequest.urlInfo
    val originalLatitude                                      = queryParameters.getOrElse(LatitudeKey, "39").toFloat
    val originalLongitude                                     = queryParameters.getOrElse(LongitudeKey, "116").toFloat

    for {
      latitudeSteps  <- -Length to Length if latitudeSteps != 0; latitudeDelta   = latitudeSteps / Precision
      longitudeSteps <- -Length to Length if longitudeSteps != 0; longitudeDelta = longitudeSteps / Precision
    } yield {
      val updatedQueryParameters = queryParameters + (LatitudeKey -> (originalLatitude + latitudeDelta).toString) + (LongitudeKey -> (originalLongitude + longitudeDelta).toString) + (OffsetKey -> "0")
      fetchResponse.fetchRequest.urlInfo.copy(queryParameters = updatedQueryParameters, deep = deep + 1)
    }
  }

  private def getChildLinksByOffset(fetchResponse: FetchResponse): Seq[UrlInfo] = {
    val urlInfo = fetchResponse.fetchRequest.urlInfo
    val offset  = urlInfo.queryParameters.getOrElse(OffsetKey, "0").toInt
    Seq(urlInfo.copy(queryParameters = urlInfo.queryParameters + (OffsetKey -> (offset + 1).toString)))
  }

  private def persist(restaurants: Seq[Restaurant]): Unit = restaurants.foreach(restaurantRepo.insertOrUpdate)

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val restaurants = Try {
      Json.parse(fetchResponse.content) match {
        case JsArray(restaurantsJsValue) =>
          restaurantsJsValue.flatMap { restaurant =>
            restaurant.validate[Restaurant].asOpt
          }
        case _ => Seq.empty[Restaurant]
      }
    } getOrElse {
      Seq.empty[Restaurant]
    }

    persist(restaurants.toList)

    ParseResult(
      fetchResponse = fetchResponse,
      childLink = if (restaurants.isEmpty) getChildLinksByLocation(fetchResponse) else getChildLinksByOffset(fetchResponse)
    )
  }
}

object RestaurantParseService {
  final val name = "ElemeRestaurantParseService"
}
