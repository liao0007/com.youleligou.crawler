package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao
import com.youleligou.eleme.models.{Food, FoodSku}

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
    createdDate: java.sql.Date = java.sql.Date.valueOf(LocalDate.now()),
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends SnapshotDao

object FoodSnapshotDao {

  implicit def fromModel(model: Food): FoodSnapshotDao = FoodSnapshotDao(
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
  implicit def fromModel(source: Seq[Food])(implicit converter: Food => FoodSnapshotDao): Seq[FoodSnapshotDao] =
    source map converter

  implicit def toModel(dao: FoodSnapshotDao): Food = Food(
    itemId = dao.itemId,
    restaurantId = dao.restaurantId,
    categoryId = dao.categoryId,
    name = dao.name,
    description = dao.description,
    monthSales = dao.monthSales,
    rating = dao.rating,
    ratingCount = dao.ratingCount,
    satisfyCount = dao.satisfyCount,
    satisfyRate = dao.satisfyRate,
    specFoods = Seq.empty[FoodSku]
  )
  implicit def toModel(source: Seq[FoodSnapshotDao])(implicit converter: FoodSnapshotDao => Food): Seq[Food] =
    source map converter

}
