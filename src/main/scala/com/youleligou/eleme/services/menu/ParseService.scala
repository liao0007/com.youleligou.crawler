package com.youleligou.eleme.services.menu

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.eleme.daos.FoodSnapshotDao
import com.youleligou.eleme.models.{FoodSnapshot, MenuSnapshot}
import play.api.libs.json._

class ParseService @Inject()(foodSnapshotRepo: CassandraRepo[FoodSnapshotDao]) extends com.youleligou.crawler.services.ParseService {

  private def persist(foodSnapshotDaos: Seq[FoodSnapshotDao]) = foodSnapshotRepo.save(foodSnapshotDaos)

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val menuSnapshots: Seq[MenuSnapshot] = Json.parse(fetchResponse.content) match {
      case JsArray(value) =>
        value flatMap { item =>
          item.validate[MenuSnapshot].asOpt
        }
      case _ => Seq.empty[MenuSnapshot]
    }

    val foodSnapshotDaos: Seq[FoodSnapshotDao] = menuSnapshots flatMap { implicit menuSnapshot =>
      val foodSnapshotDaos: Seq[FoodSnapshotDao] = menuSnapshot.foods
      foodSnapshotDaos
    }

    persist(foodSnapshotDaos)

    ParseResult(
      fetchResponse = fetchResponse,
      childLink = Seq.empty[UrlInfo]
    )
  }
}
