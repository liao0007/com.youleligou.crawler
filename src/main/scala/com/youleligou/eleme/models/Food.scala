package com.youleligou.eleme.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Json, OFormat, Reads}

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
    satisfyRate: Float
)

object Food {
  implicit val format: OFormat[Food] = Json.format[Food]
}
