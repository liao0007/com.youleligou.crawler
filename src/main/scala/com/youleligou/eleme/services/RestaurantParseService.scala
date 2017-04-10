package com.youleligou.eleme.services

import com.google.inject.Inject
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.crawler.actors.AbstractFetchActor.Fetched
import com.youleligou.crawler.services.ParseService
import com.youleligou.eleme.daos.{Restaurant, RestaurantRepo}
import play.api.libs.json._

import scala.collection.immutable
import scala.util.Try
import scala.util.control.NonFatal

class RestaurantParseService @Inject()(restaurantRepo: RestaurantRepo) extends ParseService {

  final val Precision: Float = 1000F
  final val LatitudeKey      = "latitude"
  final val LongitudeKey     = "latitude"

  private def getChildLinksByLocation(fetchResponse: FetchResponse): Seq[UrlInfo] = {
    val UrlInfo(host, queryParameters, urlType, deep) = fetchResponse.fetchRequest.urlInfo
    val originalLatitude                              = queryParameters.getOrElse(LatitudeKey, "39").toFloat
    val originalLongitude                             = queryParameters.getOrElse(LongitudeKey, "116").toFloat

    for {
      latitudeSteps  <- -5 to 5 if latitudeSteps != 0; latitudeDelta   = latitudeSteps / Precision
      longitudeSteps <- -5 to 5 if longitudeSteps != 0; longitudeDelta = longitudeSteps / Precision
    } yield {
      val updatedQueryParameters = queryParameters + (LatitudeKey -> (originalLatitude + latitudeDelta).toString) + (LongitudeKey -> (originalLongitude + longitudeDelta).toString)
      fetchResponse.fetchRequest.urlInfo.copy(queryParameters = updatedQueryParameters, deep = deep + 1)
    }
  }

  private def getChildLinksByOffset(fetchResponse: FetchResponse): Seq[UrlInfo] = {
    val urlInfo = fetchResponse.fetchRequest.urlInfo
    val offset  = urlInfo.queryParameters.getOrElse("offset", "0").toInt
    Seq(urlInfo.copy(queryParameters = urlInfo.queryParameters + ("offset" -> (offset + 1).toString)))
  }

  private def persist(restaurants: Seq[Restaurant]) = restaurantRepo.create(restaurants.toList)

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
  final val name = "RestaurantParseService"
}
