package com.youleligou.meituan.modals

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

/**
  * Created by liangliao on 8/5/17.
  */
case class Sku(
    id: Long,
    spec: Option[String],
    description: Option[String],
    picture: String,
    price: Float,
    originPrice: Float,
    boxNum: Float,
    boxPrice: Float,
    minOrderCount: Int,
    status: Int,
    stock: Int,
    realStock: Int,
    activityStock: Int,
    restrict: Int,
    promotionInfo: Option[String]
)

object Sku {
  implicit val poiReads: Reads[Sku] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "spec").readNullable[String] and
      (JsPath \ "description").readNullable[String] and
      (JsPath \ "picture").read[String] and
      (JsPath \ "price").read[Float] and
      (JsPath \ "origin_price").read[Float] and
      (JsPath \ "box_num").read[Float] and
      (JsPath \ "box_price").read[Float] and
      (JsPath \ "min_order_count").read[Int] and
      (JsPath \ "status").read[Int] and
      (JsPath \ "stock").read[Int] and
      (JsPath \ "real_stock").read[Int] and
      (JsPath \ "activity_stock").read[Int] and
      (JsPath \ "restrict").read[Int] and
      (JsPath \ "promotion_info").readNullable[String]
  )(Sku.apply _)
}
