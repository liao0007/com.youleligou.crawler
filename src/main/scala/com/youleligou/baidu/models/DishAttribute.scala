package com.youleligou.baidu.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

/**
  * Created by liangliao on 23/4/17.
  */
case class DishAttribute(
    id: String,
    dishAttrId: Long,
    name: String,
    price: Float
)

object DishAttribute {
  implicit val shopReads: Reads[DishAttribute] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "dish_attr_id").read[String].map(_.toLong) and
      (JsPath \ "name").read[String] and
      (JsPath \ "price").read[Float]
  )(DishAttribute.apply _)
}
