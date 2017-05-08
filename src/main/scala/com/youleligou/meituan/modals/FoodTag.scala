package com.youleligou.meituan.modals

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

/**
  * Created by liangliao on 8/5/17.
  */
case class FoodTag( // category
                    tag: Long,
                    name: String,
                    icon: String,
                    typ: Int,
                    spus: Seq[Spu])

object FoodTag {
  implicit val poiReads: Reads[FoodTag] = (
    (JsPath \ "tag").read[String].map(_.toLong) and
      (JsPath \ "name").read[String] and
      (JsPath \ "icon").read[String] and
      (JsPath \ "typ").read[Int] and
      (JsPath \ "spus").read[Seq[Spu]]
  )(FoodTag.apply _)
}
