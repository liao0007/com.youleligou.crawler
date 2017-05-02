package com.youleligou.eleme.daos

import com.youleligou.eleme.models.RestaurantSnapshot
import org.joda.time.{DateTime, LocalDate}

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
    createdDate: LocalDate = LocalDate.now(),
    createdAt: DateTime = DateTime.now()
)

object RestaurantSnapshotDao {
  implicit def fromModel(model: RestaurantSnapshot): RestaurantSnapshotDao = RestaurantSnapshotDao(
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

  implicit def convertSeq(source: Seq[RestaurantSnapshot])(implicit converter: RestaurantSnapshot => RestaurantSnapshotDao): Seq[RestaurantSnapshotDao] = source map converter
}