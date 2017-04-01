package com.youleligou.crawler.service.parse.json

import com.google.inject.Inject
import com.youleligou.crawler.model.{FetchResult, ParseResult, UrlInfo}
import com.youleligou.crawler.service.parse.ParseService
import dao.{Canteen, CanteenRepo}
import play.api.libs.json.{JsArray, Json}

/**
  * Created by young.yang on 2016/8/31.
  * Jsoup解析器
  */
class CanteenParseService @Inject()(canteenRepo: CanteenRepo) extends ParseService {

  private def parserUrls(urlInfo: UrlInfo, result: JsArray): Seq[UrlInfo] = {
    val queryParameters = urlInfo.queryParameters
    if (result.value.nonEmpty) {
      val latitude = queryParameters("latitude")
      val longitude = queryParameters("longitude")
      val offset = (queryParameters("offset").toInt + 1).toString
      Seq(
        urlInfo.copy(queryParameters = Map[String, String](
          "latitude" -> latitude,
          "longitude" -> longitude,
          "offset" -> offset
        ),
          deep = urlInfo.deep + 1)
      )
    } else {
      for {
        latitudeDelta <- -5 to 5
        longitudeDelta <- -5 to 5
      } yield {
        val latitude = (queryParameters("latitude").toFloat + latitudeDelta / 2).toString
        val longitude = (queryParameters("longitude").toFloat + longitudeDelta / 2).toString
        val offset = "0"
        urlInfo.copy(queryParameters = Map[String, String](
          "latitude" -> latitude,
          "longitude" -> longitude,
          "offset" -> offset
        ),
          deep = urlInfo.deep + 1)
      }
    }
  }

  private def persist(canteensJsArray: JsArray) = {
    val canteens = canteensJsArray.value.flatMap { canteenJsValue =>
      canteenJsValue.validate[Canteen].asOpt
    }
    canteenRepo.create(canteens.toList)
  }

  /**
    * 解析具体实现
    */
  override def parse(fetchResult: FetchResult): ParseResult = {
    val canteens = Json.parse(fetchResult.content).as[JsArray]
    persist(canteens)
    ParseResult(
      urlInfo = fetchResult.urlInfo,
      content = Json.stringify(canteens),
      publishTime = System.currentTimeMillis(),
      updateTime = System.currentTimeMillis(),
      childLink = parserUrls(fetchResult.urlInfo, canteens).toList
    )
  }
}
