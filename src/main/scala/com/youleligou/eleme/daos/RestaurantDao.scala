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

  implicit def toModel(dao: RestaurantDao): Restaurant = Restaurant(
    id = dao.id,
    name = dao.name,
    address = dao.address,
    imagePath = dao.imagePath,
    latitude = dao.latitude,
    longitude = dao.longitude,
    identification = Some(Identification(dao.licensesNumber, dao.companyName))
  )

  implicit def convertDaoSeq(source: Seq[Restaurant])(implicit converter: Restaurant => RestaurantDao): Seq[RestaurantDao] =
    source map converter
  implicit def convertToModelSeq(source: Seq[RestaurantDao])(implicit converter: RestaurantDao => Restaurant): Seq[Restaurant] =
    source map converter
}
