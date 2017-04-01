package com.youleligou.crawler.service.parse

import com.google.inject.Inject
import com.youleligou.crawler.model.UrlInfo.GenerateType
import com.youleligou.crawler.model.{FetchResult, ParseResult, UrlInfo}
import dao.{Canteen, CanteenRepo}
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import play.api.libs.json.{JsArray, JsResult, JsValue, Json}

import scala.collection.JavaConverters._

/**
  * Created by young.yang on 2016/8/31.
  * Jsoup解析器
  */
class JsonParseService @Inject()(canteenRepo: CanteenRepo) extends ParseService {

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
      url = fetchResult.url,
      content = Json.stringify(canteens),
      publishTime = System.currentTimeMillis(),
      updateTime = System.currentTimeMillis(),
      childLink = List.empty[UrlInfo]
    )
  }
}
