package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao
import com.youleligou.eleme.models.{Identification, Restaurant}

case class RestaurantSnapshotDao(
    id: Long,
    address: String,
    averageCost: Option[String],
    description: String,
    deliveryFee: Float,
    minimumOrderAmount: Float,
    imagePath: String,
    isNew: Boolean,
    isPremium: Boolean,
    latitude: Float,
    longitude: Float,
    name: String,
    phone: Option[String],
    promotionInfo: String,
    rating: Float,
    ratingCount: Int,
    recentOrderNum: Int,
    licensesNumber: Option[String],
    companyName: Option[String],
    status: Int,
    createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends SnapshotDao

object RestaurantSnapshotDao {
  implicit def fromModel(model: Restaurant): RestaurantSnapshotDao = RestaurantSnapshotDao(
    id = model.id,
    address = model.address,
    averageCost = model.averageCost,
    description = model.description,
    deliveryFee = model.deliveryFee,
    minimumOrderAmount = model.minimumOrderAmount,
    imagePath = model.imagePath,
    isNew = model.isNew,
    isPremium = model.isPremium,
    latitude = model.latitude,
    longitude = model.longitude,
    name = model.name,
    phone = model.phone,
    promotionInfo = model.promotionInfo,
    rating = model.rating,
    ratingCount = model.ratingCount,
    recentOrderNum = model.recentOrderNum,
    licensesNumber = model.identification.flatMap(_.licensesNumber),
    companyName = model.identification.flatMap(_.companyName),
    status = model.status
  )
  implicit def fromModel(source: Seq[Restaurant])(implicit converter: Restaurant => RestaurantSnapshotDao): Seq[RestaurantSnapshotDao] =
    source map converter

  implicit def toModel(dao: RestaurantSnapshotDao): Restaurant = Restaurant(
    id = dao.id,
    address = dao.address,
    averageCost = dao.averageCost,
    description = dao.description,
    deliveryFee = dao.deliveryFee,
    minimumOrderAmount = dao.minimumOrderAmount,
    imagePath = dao.imagePath,
    isNew = dao.isNew,
    isPremium = dao.isPremium,
    latitude = dao.latitude,
    longitude = dao.longitude,
    name = dao.name,
    phone = dao.phone,
    promotionInfo = dao.promotionInfo,
    rating = dao.rating,
    ratingCount = dao.ratingCount,
    recentOrderNum = dao.recentOrderNum,
    identification = Some(Identification(dao.licensesNumber, dao.companyName)),
    status = dao.status
  )
  implicit def toModel(source: Seq[RestaurantSnapshotDao])(implicit converter: RestaurantSnapshotDao => Restaurant): Seq[Restaurant] =
    source map converter
}
