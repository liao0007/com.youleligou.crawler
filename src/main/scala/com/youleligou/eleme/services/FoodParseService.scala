package com.youleligou.eleme.services

import com.google.inject.Inject
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.crawler.services.ParseService
import com.youleligou.eleme.daos.{Food, FoodRepo}
import play.api.libs.json._

class FoodParseService @Inject()(foodRepo: FoodRepo) extends ParseService {

  private def persist(food: Seq[Food]) = food.foreach(foodRepo.insertOrUpdate)

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val food =
      Json.parse(fetchResponse.content) \\ "foods" flatMap {
        case JsArray(value) =>
          value flatMap { item =>
            item.validate[Food].asOpt
          }
        case _ => None
      }

    persist(food.toList)

    ParseResult(
      fetchResponse = fetchResponse,
      childLink = Seq.empty[UrlInfo]
    )
  }
}

object FoodParseService {
  final val name = "ElemeFoodParseService"
}
