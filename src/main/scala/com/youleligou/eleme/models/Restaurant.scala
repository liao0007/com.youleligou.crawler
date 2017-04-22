package com.youleligou.eleme.models

import org.joda.time.{DateTime, LocalDate}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Json, OFormat, Reads}

/**
  * Created by liangliao on 23/4/17.
  */
case class Restaurant(
    id: Long,
    address: String,
    averageCost: String,
    description: String,
    deliveryFee: Float,
    minimumOrderAmount: Float,
    imagePath: String,
    isNew: Boolean,
    isPremium: Boolean,
    latitude: Float,
    longitude: Float,
    name: String,
    phone: String,
    promotionInfo: String,
    rating: Float,
    ratingCount: Int,
    recentOrderNum: Int,
    identification: Option[Identification] = None,
    status: Int
)

object Restaurant {
  implicit val format: OFormat[Restaurant] = Json.format[Restaurant]
}
