package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Date

import com.youleligou.eleme.models.{Identification, Restaurant}

case class RestaurantDao(
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

object RestaurantDao {

  /*
  model <-> dao
   */
  implicit def fromModel(model: Restaurant): RestaurantDao = RestaurantDao(
    id = model.id,
    name = model.name,
    address = model.address,
    imagePath = model.imagePath,
    latitude = model.latitude,
    longitude = model.longitude,
    licensesNumber = model.identification.flatMap(_.licensesNumber),
    companyName = model.identification.flatMap(_.companyName)
  )

  implicit def fromModel(source: Seq[Restaurant])(implicit converter: Restaurant => RestaurantDao): Seq[RestaurantDao] =
    source map converter

  implicit def toModel(dao: RestaurantDao): Restaurant = Restaurant(
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

  implicit def toModel(source: Seq[RestaurantDao])(implicit converter: RestaurantDao => Restaurant): Seq[Restaurant] =
    source map converter

  /*
  search <-> dao
   */
  implicit def fromSearch(search: RestaurantSearch): RestaurantDao = RestaurantDao(
    id = search.id,
    name = search.name,
    address = search.address,
    imagePath = search.imagePath,
    latitude = search.latitude,
    longitude = search.longitude,
    licensesNumber = search.identification.flatMap(_.licensesNumber),
    companyName = search.identification.flatMap(_.companyName)
  )

  implicit def fromSearch(source: Seq[RestaurantSearch])(implicit converter: RestaurantSearch => RestaurantDao): Seq[RestaurantDao] =
    source map converter

  implicit def toSearch(dao: RestaurantDao): RestaurantSearch = RestaurantSearch(
    id = dao.id,
    name = dao.name,
    address = dao.address,
    location = Map(
      "lat" -> dao.latitude,
      "lon" -> dao.longitude
    ),
    identification = Some(Identification(dao.licensesNumber, dao.companyName))
  )

  implicit def toSearch(source: Seq[RestaurantDao])(implicit converter: RestaurantDao => RestaurantSearch): Seq[RestaurantSearch] =
    source map converter
}
