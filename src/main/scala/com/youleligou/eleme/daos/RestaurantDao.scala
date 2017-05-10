package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Date

import com.youleligou.core.daos.Dao
import com.youleligou.eleme.models.{Identification, Restaurant}

case class RestaurantDao(
    restaurantId: Long,
    //PK
    name: String,
    address: String,
    imagePath: String,
    latitude: Float,
    longitude: Float,
    licensesNumber: Option[String] = None,
    companyName: Option[String] = None,
    createdAt: Date = Timestamp.valueOf(LocalDateTime.now())
) extends Dao

object RestaurantDao {

  /*
  model <-> dao
   */
  implicit def fromModel(model: Restaurant): RestaurantDao = RestaurantDao(
    restaurantId = model.id,
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
    id = dao.restaurantId,
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

  implicit def fromSnapshot(snapshot: RestaurantSnapshotDao): RestaurantDao = RestaurantDao(
    restaurantId = snapshot.restaurantId,
    name = snapshot.name,
    address = snapshot.address,
    imagePath = snapshot.imagePath,
    latitude = snapshot.latitude,
    longitude = snapshot.longitude,
    licensesNumber = snapshot.licensesNumber,
    companyName = snapshot.companyName,
    createdAt = snapshot.createdAt
  )
  implicit def fromSnapshot(source: Seq[RestaurantSnapshotDao])(implicit converter: RestaurantSnapshotDao => RestaurantDao): Seq[RestaurantDao] =
    source map converter

}
