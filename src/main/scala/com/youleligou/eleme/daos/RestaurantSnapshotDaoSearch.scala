package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao
import com.youleligou.eleme.models.Identification

case class RestaurantSnapshotDaoSearch(
    id: String,
    restaurantId: Long,
    address: String,
    averageCost: Option[String],
    description: String,
    deliveryFee: Float,
    minimumOrderAmount: Float,
    imagePath: String,
    isNew: Boolean,
    isPremium: Boolean,
    name: String,
    phone: Option[String],
    promotionInfo: String,
    rating: Float,
    ratingCount: Int,
    recentOrderNum: Int,
    status: Int,
    location: Map[String, Float],
    identification: Option[Identification],
    createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends SnapshotDao

object RestaurantSnapshotDaoSearch {

  implicit def fromDao(dao: RestaurantSnapshotDao): RestaurantSnapshotDaoSearch = RestaurantSnapshotDaoSearch(
    id = s"${dao.id}-${dao.createdDate}",
    restaurantId = dao.id,
    address = dao.address,
    averageCost = dao.averageCost,
    description = dao.description,
    deliveryFee = dao.deliveryFee,
    minimumOrderAmount = dao.minimumOrderAmount,
    imagePath = dao.imagePath,
    isNew = dao.isNew,
    isPremium = dao.isPremium,
    name = dao.name,
    phone = dao.phone,
    promotionInfo = dao.promotionInfo,
    rating = dao.rating,
    ratingCount = dao.ratingCount,
    recentOrderNum = dao.recentOrderNum,
    status = dao.status,
    location = Map(
      "lat" -> dao.latitude,
      "lon" -> dao.longitude
    ),
    identification = Some(Identification(dao.licensesNumber, dao.companyName)),
    createdDate = dao.createdDate,
    createdAt = dao.createdAt
  )
  implicit def fromDao(source: Seq[RestaurantSnapshotDao])(
      implicit converter: RestaurantSnapshotDao => RestaurantSnapshotDaoSearch): Seq[RestaurantSnapshotDaoSearch] =
    source map converter

  implicit def toDao(search: RestaurantSnapshotDaoSearch): RestaurantSnapshotDao = RestaurantSnapshotDao(
    id = search.restaurantId,
    address = search.address,
    averageCost = search.averageCost,
    description = search.description,
    deliveryFee = search.deliveryFee,
    minimumOrderAmount = search.minimumOrderAmount,
    imagePath = search.imagePath,
    isNew = search.isNew,
    isPremium = search.isPremium,
    latitude = search.location("lat"),
    longitude = search.location("log"),
    name = search.name,
    phone = search.phone,
    promotionInfo = search.promotionInfo,
    rating = search.rating,
    ratingCount = search.ratingCount,
    recentOrderNum = search.recentOrderNum,
    licensesNumber = search.identification.flatMap(_.licensesNumber),
    companyName = search.identification.flatMap(_.companyName),
    status = search.status,
    createdDate = search.createdDate,
    createdAt = search.createdAt
  )
  implicit def toDao(source: Seq[RestaurantSnapshotDaoSearch])(
      implicit converter: RestaurantSnapshotDaoSearch => RestaurantSnapshotDao): Seq[RestaurantSnapshotDao] =
    source map converter

}
