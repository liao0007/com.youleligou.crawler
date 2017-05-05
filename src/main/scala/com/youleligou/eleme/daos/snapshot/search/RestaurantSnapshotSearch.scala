package com.youleligou.eleme.daos.snapshot.search

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.eleme.models.{Identification, Restaurant}

case class RestaurantSnapshotSearch(
    id: Long,
    name: String,
    address: String,
    location: Map[String, Float],
    identification: Option[Identification],
    createdDate: java.sql.Date = java.sql.Date.valueOf(LocalDate.now()),
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
)

object RestaurantSnapshotSearch {

  implicit def fromModel(model: Restaurant): RestaurantSnapshotSearch = RestaurantSnapshotSearch(
    id = model.id,
    name = model.name,
    address = model.address,
    location = Map(
      "lat" -> model.latitude,
      "lon" -> model.longitude
    ),
    identification = model.identification
  )
  implicit def fromModel(source: Seq[Restaurant])(implicit converter: Restaurant => RestaurantSnapshotSearch): Seq[RestaurantSnapshotSearch] =
    source map converter

  implicit def toModel(dao: RestaurantSnapshotSearch): Restaurant = Restaurant(
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
  implicit def toModel(source: Seq[RestaurantSnapshotSearch])(implicit converter: RestaurantSnapshotSearch => Restaurant): Seq[Restaurant] =
    source map converter
}
