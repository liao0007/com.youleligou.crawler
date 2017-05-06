package com.youleligou.eleme.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

/**
  * Created by liangliao on 5/5/17.
  */
case class FoodSku(
    originalPrice: Option[Float],
    skuId: Long,
    name: String,
    restaurantId: Long,
    foodId: Long,
    packingFee: Float,
    recentRating: Float,
    promotionStock: Int,
    price: Float,
    soldOut: Boolean,
    recentPopularity: Int,
    isEssential: Boolean,
    itemId: Long,
    checkoutMode: Int,
    stock: Int
)

object FoodSku {
  implicit val restaurantReads: Reads[FoodSku] = (
    (JsPath \ "original_price").readNullable[Float] and
      (JsPath \ "sku_id").read[String].map(_.toLong) and
      (JsPath \ "name").read[String] and
      (JsPath \ "restaurant_id").read[Long] and
      (JsPath \ "food_id").read[Long] and
      (JsPath \ "packing_fee").read[Float] and
      (JsPath \ "recent_rating").read[Float] and
      (JsPath \ "promotion_stock").read[Int] and
      (JsPath \ "price").read[Float] and
      (JsPath \ "sold_out").read[Boolean] and
      (JsPath \ "recent_popularity").read[Int] and
      (JsPath \ "is_essential").read[Boolean] and
      (JsPath \ "item_id").read[String].map(_.toLong) and
      (JsPath \ "checkout_mode").read[Int] and
      (JsPath \ "stock").read[Int]
  )(FoodSku.apply _)
}
