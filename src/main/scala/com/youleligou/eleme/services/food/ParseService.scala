package com.youleligou.eleme.services.food

import com.google.inject.Inject
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.eleme.daos.{Food, FoodRepo}
import play.api.libs.json._

import scala.concurrent.Future

class ParseService @Inject()(foodRepo: FoodRepo) extends com.youleligou.crawler.services.ParseService {

  private def persist(food: Seq[Food]): Future[Option[Int]] = foodRepo.create(food.toList)

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