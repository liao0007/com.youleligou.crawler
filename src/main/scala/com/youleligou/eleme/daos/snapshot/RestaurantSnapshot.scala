package com.youleligou.eleme.daos.snapshot

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.eleme.daos.snapshot.search.RestaurantSnapshotSearch
import com.youleligou.eleme.models.{Identification, Restaurant}

case class RestaurantSnapshot(
    id: Long,
    address: String,
    averageCost: Option[Float],
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
    createdDate: java.sql.Date = java.sql.Date.valueOf(LocalDate.now()),
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
)

object RestaurantSnapshot {
  implicit def fromModel(model: Restaurant): RestaurantSnapshot = RestaurantSnapshot(
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
  implicit def fromModel(source: Seq[Restaurant])(implicit converter: Restaurant => RestaurantSnapshot): Seq[RestaurantSnapshot] =
    source map converter

  implicit def toModel(dao: RestaurantSnapshot): Restaurant = Restaurant(
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
  implicit def toModel(source: Seq[RestaurantSnapshot])(implicit converter: RestaurantSnapshot => Restaurant): Seq[Restaurant] =
    source map converter

  implicit def fromSearch(search: RestaurantSnapshotSearch): RestaurantSnapshot = RestaurantSnapshot(
    id = search.id,
    address = search.address,
    averageCost = search.averageCost,
    description = search.description,
    deliveryFee = search.deliveryFee,
    minimumOrderAmount = search.minimumOrderAmount,
    imagePath = search.imagePath,
    isNew = search.isNew,
    isPremium = search.isPremium,
    latitude = search.latitude,
    longitude = search.longitude,
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

  implicit def fromSearch(source: Seq[RestaurantSnapshotSearch])(
      implicit converter: RestaurantSnapshotSearch => RestaurantSnapshot): Seq[RestaurantSnapshot] =
    source map converter

  implicit def toSearch(dao: RestaurantSnapshot): RestaurantSnapshotSearch = RestaurantSnapshotSearch(
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
    licensesNumber = dao.identification.flatMap(_.licensesNumber),
    companyName = dao.identification.flatMap(_.companyName),
    status = dao.status
  )

  implicit def toSearch(source: Seq[Restaurant])(implicit converter: Restaurant => RestaurantSnapshot): Seq[RestaurantSnapshot] =
    source map converter
}
