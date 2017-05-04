package com.youleligou.eleme.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

/**
  * Created by liangliao on 4/5/17.
  */
case class Category(
    id: Long,
    typ: Int,
    isActivity: Option[Boolean] = None,
    activity: Option[String] = None,
    description: String,
    iconUrl: String,
    name: String,
    foods: Seq[Food]
)

object Category {
  implicit val categoryReads: Reads[Category] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "type").read[Int] and
      (JsPath \ "is_activity").readNullable[Boolean] and
      (JsPath \ "activity").readNullable[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "icon_url").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "foods").read[Seq[Food]]
  )(Category.apply _)
}
