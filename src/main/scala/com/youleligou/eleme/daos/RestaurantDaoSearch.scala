package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Date

import com.youleligou.core.daos.Dao
import com.youleligou.eleme.models.Identification

case class RestaurantDaoSearch(
    id: Long,
    name: String,
    address: String,
    imagePath: String,
    location: Map[String, Float],
    identification: Option[Identification],
    createdAt: Date = Timestamp.valueOf(LocalDateTime.now())
) extends Dao

object RestaurantDaoSearch {

  implicit def fromDao(dao: RestaurantDao): RestaurantDaoSearch = RestaurantDaoSearch(
    id = dao.id,
    address = dao.address,
    imagePath = dao.imagePath,
    name = dao.name,
    location = Map(
      "lat" -> dao.latitude,
      "lon" -> dao.longitude
    ),
    identification = Some(Identification(dao.licensesNumber, dao.companyName)),
    createdAt = dao.createdAt
  )
  implicit def fromDao(source: Seq[RestaurantDao])(implicit converter: RestaurantDao => RestaurantDaoSearch): Seq[RestaurantDaoSearch] =
    source map converter

  implicit def toDao(search: RestaurantDaoSearch): RestaurantDao = RestaurantDao(
    id = search.id,
    address = search.address,
    imagePath = search.imagePath,
    latitude = search.location("lat"),
    longitude = search.location("log"),
    name = search.name,
    licensesNumber = search.identification.flatMap(_.licensesNumber),
    companyName = search.identification.flatMap(_.companyName),
    createdAt = search.createdAt
  )
  implicit def toDao(source: Seq[RestaurantDaoSearch])(implicit converter: RestaurantDaoSearch => RestaurantDao): Seq[RestaurantDao] =
    source map converter

}
