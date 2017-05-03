package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.{Instant, LocalDateTime}

import com.youleligou.eleme.models.FoodSnapshot
import org.joda.time.DateTime

case class FoodSnapshotDao(
    itemId: Long,
    restaurantId: Long,
    categoryId: Long,
    name: String,
    description: String,
    monthSales: Int,
    rating: Float,
    ratingCount: Int,
    satisfyCount: Int,
    satisfyRate: Float,
    createdAt: Timestamp = Timestamp.valueOf(LocalDateTime.now())
)

object FoodSnapshotDao {

  implicit def fromModel(model: FoodSnapshot): FoodSnapshotDao = FoodSnapshotDao(
    itemId = model.itemId,
    restaurantId = model.restaurantId,
    categoryId = model.categoryId,
    name = model.name,
    description = model.description,
    monthSales = model.monthSales,
    rating = model.rating,
    ratingCount = model.ratingCount,
    satisfyCount = model.satisfyCount,
    satisfyRate = model.satisfyRate
  )

  implicit def convertSeq(source: Seq[FoodSnapshot])(implicit converter: FoodSnapshot => FoodSnapshotDao): Seq[FoodSnapshotDao] = source map converter

}
