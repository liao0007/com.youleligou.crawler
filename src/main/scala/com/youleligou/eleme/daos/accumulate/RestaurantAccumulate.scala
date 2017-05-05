package com.youleligou.eleme.daos.accumulate

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Date

import com.youleligou.eleme.daos.accumulate.search.RestaurantAccumulateSearch
import com.youleligou.eleme.models.{Identification, Restaurant}

case class RestaurantAccumulate(
    id: Long,
    name: String,
    address: String,
    imagePath: String,
    latitude: Float,
    longitude: Float,
    licensesNumber: Option[String] = None,
    companyName: Option[String] = None,
    createdAt: Date = Timestamp.valueOf(LocalDateTime.now())
)

object RestaurantAccumulate {

  /*
  model <-> dao
   */
  implicit def fromModel(model: Restaurant): RestaurantAccumulate = RestaurantAccumulate(
    id = model.id,
    name = model.name,
    address = model.address,
    imagePath = model.imagePath,
    latitude = model.latitude,
    longitude = model.longitude,
    licensesNumber = model.identification.flatMap(_.licensesNumber),
    companyName = model.identification.flatMap(_.companyName)
  )
  implicit def fromModel(source: Seq[Restaurant])(implicit converter: Restaurant => RestaurantAccumulate): Seq[RestaurantAccumulate] =
    source map converter

  implicit def toModel(dao: RestaurantAccumulate): Restaurant = Restaurant(
    id = dao.id,
    address = dao.address,
    averageCost = None,
    description = "",
    deliveryFee = 0,
    minimumOrderAmount = 0,
    imagePath = dao.imagePath,
    isNew = false,
    isPremium = false,
    latitude = dao.latitude,
    longitude = dao.longitude,
    name = dao.name,
    phone = None,
    promotionInfo = "",
    rating = 0,
    ratingCount = 0,
    recentOrderNum = 0,
    identification = Some(Identification(dao.licensesNumber, dao.companyName)),
    status = 0
  )
  implicit def toModel(source: Seq[RestaurantAccumulate])(implicit converter: RestaurantAccumulate => Restaurant): Seq[Restaurant] =
    source map converter

  /*
  search <-> dao
   */
  implicit def fromSearch(search: RestaurantAccumulateSearch): RestaurantAccumulate = RestaurantAccumulate(
    id = search.id,
    name = search.name,
    address = search.address,
    imagePath = "",
    latitude = search.location("lat"),
    longitude = search.location("lon"),
    licensesNumber = search.identification.flatMap(_.licensesNumber),
    companyName = search.identification.flatMap(_.companyName),
    createdAt = search.createdAt
  )

  implicit def fromSearch(source: Seq[RestaurantAccumulateSearch])(
      implicit converter: RestaurantAccumulateSearch => RestaurantAccumulate): Seq[RestaurantAccumulate] =
    source map converter

  implicit def toSearch(dao: RestaurantAccumulate): RestaurantAccumulateSearch = RestaurantAccumulateSearch(
    id = dao.id,
    name = dao.name,
    address = dao.address,
    location = Map(
      "lat" -> dao.latitude,
      "lon" -> dao.longitude
    ),
    identification = Some(Identification(dao.licensesNumber, dao.companyName)),
    createdAt = dao.createdAt
  )

  implicit def toSearch(source: Seq[RestaurantAccumulate])(
      implicit converter: RestaurantAccumulate => RestaurantAccumulateSearch): Seq[RestaurantAccumulateSearch] =
    source map converter
}
