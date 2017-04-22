package com.youleligou.eleme.services.food

import com.google.inject.Inject
import com.outworkers.phantom.database.DatabaseProvider
import com.outworkers.phantom.dsl.ResultSet
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.eleme.daos.cassandra.{ElemeDatabase, FoodDao}
import com.youleligou.eleme.models.Food
import play.api.libs.json._

import scala.concurrent.Future

class ParseService @Inject()(val database: ElemeDatabase) extends com.youleligou.crawler.services.ParseService with DatabaseProvider[ElemeDatabase] {

  private def persist(foods: Seq[Food]): Seq[Future[ResultSet]] = {
    database.foods.insertOrUpdate(foods)
  }

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val foods =
      Json.parse(fetchResponse.content) \\ "foods" flatMap {
        case JsArray(value) =>
          value flatMap { item =>
            item.validate[Food].asOpt
          }
        case _ => None
      }

    persist(foods)

    ParseResult(
      fetchResponse = fetchResponse,
      childLink = Seq.empty[UrlInfo]
    )
  }
}
