package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.Instant

import com.youleligou.eleme.models.Restaurant
import org.joda.time.DateTime

case class RestaurantDao(
    id: Long,
    name: String,
    address: String,
    imagePath: String,
    latitude: Float,
    longitude: Float,
    licensesNumber: Option[String] = None,
    companyName: Option[String] = None,
    createdAt: Timestamp = new Timestamp(DateTime.now().getMillis)
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

  implicit def convertSeq(source: Seq[Restaurant])(implicit converter: Restaurant => RestaurantDao): Seq[RestaurantDao] =
    source map converter
}
