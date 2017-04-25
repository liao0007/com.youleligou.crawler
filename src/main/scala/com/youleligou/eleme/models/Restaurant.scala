package com.youleligou.eleme.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

/**
  * Created by liangliao on 23/4/17.
  */
case class Restaurant(
    id: Long,
    address: String,
    averageCost: Option[String],
    description: String,
    deliveryFee: Float,
    minimumOrderAmount: Float,
    imagePath: String,
    isNew: Boolean,
    isPremium: Boolean,
    latitude: Float,
    longitude: Float,
    name: String,
    phone: Option[String],
    promotionInfo: String,
    rating: Float,
    ratingCount: Int,
    recentOrderNum: Int,
    identification: Option[Identification] = None,
    status: Int
)

object Restaurant {
  implicit val restaurantReads: Reads[Restaurant] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "address").read[String] and
      (JsPath \ "average_cost").readNullable[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "float_delivery_fee").read[Float] and
      (JsPath \ "float_minimum_order_amount").read[Float] and
      (JsPath \ "image_path").read[String] and
      (JsPath \ "is_new").read[Boolean](false) and
      (JsPath \ "is_premium").read[Boolean](false) and
      (JsPath \ "latitude").read[Float] and
      (JsPath \ "longitude").read[Float] and
      (JsPath \ "name").read[String] and
      (JsPath \ "phone").readNullable[String] and
      (JsPath \ "promotion_info").read[String] and
      (JsPath \ "rating").read[Float] and
      (JsPath \ "rating_count").read[Int] and
      (JsPath \ "recent_order_num").read[Int] and
      (JsPath \ "identification").readNullable[Identification] and
      (JsPath \ "status").read[Int]
  )(Restaurant.apply _)
}
