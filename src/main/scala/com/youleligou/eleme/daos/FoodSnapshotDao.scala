package com.youleligou.eleme.daos

import java.sql.{Date, Timestamp}
import java.time.{LocalDate, LocalDateTime}
import java.util.Date

import com.youleligou.eleme.models.{FoodSnapshot, MenuSnapshot}

case class FoodSnapshotDao(
    itemId: Long,
    restaurantId: Long,
    categoryId: Long,
    categoryName: String,
    name: String,
    description: String,
    monthSales: Int,
    rating: Float,
    ratingCount: Int,
    satisfyCount: Int,
    satisfyRate: Float,
    createdDate: java.sql.Date = java.sql.Date.valueOf(LocalDate.now()),
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
)

object FoodSnapshotDao {

  implicit def fromModel(model: FoodSnapshot)(implicit menuModel: MenuSnapshot): FoodSnapshotDao = FoodSnapshotDao(
    itemId = model.itemId,
    restaurantId = model.restaurantId,
    categoryId = model.categoryId,
    categoryName = menuModel.name,
    name = model.name,
    description = model.description,
    monthSales = model.monthSales,
    rating = model.rating,
    ratingCount = model.ratingCount,
    satisfyCount = model.satisfyCount,
    satisfyRate = model.satisfyRate
  )

  implicit def convertSeq(source: Seq[FoodSnapshot])(implicit converter: FoodSnapshot => FoodSnapshotDao): Seq[FoodSnapshotDao] =
    source map converter

}
