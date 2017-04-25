package com.youleligou.eleme.services.food

import com.google.inject.Inject
import com.youleligou.core.reps.Repo
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.eleme.daos.FoodSnapshotDao
import com.youleligou.eleme.models.Food
import play.api.libs.json._

import scala.concurrent.Future

class ParseService @Inject()(foodRepo: Repo[FoodSnapshotDao]) extends com.youleligou.crawler.services.ParseService {

  private def persist(foods: Seq[Food]): Future[Unit] = {
    foodRepo.save(foods)
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
