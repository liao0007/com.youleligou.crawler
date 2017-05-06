package com.youleligou.eleme.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

/**
  * Created by liangliao on 23/4/17.
  */
case class Food(
    itemId: Long,
    restaurantId: Long,
    categoryId: Long,
    name: String,
    description: String,
    monthSales: Int,
    rating: Float,
    ratingCount: Int,
    satisfyCount: Int,
    satisfyRate: Float,
    specFoods: Seq[FoodSku]
)

object Food {
  implicit val restaurantReads: Reads[Food] = (
    (JsPath \ "item_id").read[String].map(_.toLong) and
      (JsPath \ "restaurant_id").read[Long] and
      (JsPath \ "category_id").read[Long] and
      (JsPath \ "name").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "month_sales").read[Int] and
      (JsPath \ "rating").read[Float] and
      (JsPath \ "rating_count").read[Int] and
      (JsPath \ "satisfy_count").read[Int] and
      (JsPath \ "satisfy_rate").read[Float] and
      (JsPath \ "specfoods").read[Seq[FoodSku]]
  )(Food.apply _)
}
