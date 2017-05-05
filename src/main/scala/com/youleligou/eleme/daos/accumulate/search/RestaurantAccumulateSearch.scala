package com.youleligou.eleme.daos.accumulate.search

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Date

import com.youleligou.eleme.models.{Identification, Restaurant}

case class RestaurantAccumulateSearch(
    id: Long,
    name: String,
    address: String,
    location: Map[String, Float],
    identification: Option[Identification],
    createdAt: Date = Timestamp.valueOf(LocalDateTime.now())
)

object RestaurantAccumulateSearch {
  implicit def fromModel(model: Restaurant): RestaurantAccumulateSearch = RestaurantAccumulateSearch(
    id = model.id,
    name = model.name,
    address = model.address,
    location = Map(
      "lat" -> model.latitude,
      "lon" -> model.longitude
    ),
    identification = model.identification
  )

  implicit def fromModel(source: Seq[Restaurant])(implicit converter: Restaurant => RestaurantAccumulateSearch): Seq[RestaurantAccumulateSearch] =
    source map converter

  implicit def toModel(dao: RestaurantAccumulateSearch): Restaurant = Restaurant(
    id = dao.id,
    address = dao.address,
    averageCost = None,
    description = "",
    deliveryFee = 0,
    minimumOrderAmount = 0,
    imagePath = "",
    isNew = false,
    isPremium = false,
    latitude = dao.location("lat"),
    longitude = dao.location("lon"),
    name = dao.name,
    phone = None,
    promotionInfo = "",
    rating = 0,
    ratingCount = 0,
    recentOrderNum = 0,
    identification = dao.identification,
    status = 0
  )

  implicit def toModel(source: Seq[RestaurantAccumulateSearch])(implicit converter: RestaurantAccumulateSearch => Restaurant): Seq[Restaurant] =
    source map converter
}
