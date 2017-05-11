package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao
import com.youleligou.eleme.models.{Food, FoodSku}

case class FoodSnapshotDao(
    createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
    restaurantId: Long,
    categoryId: Long,
    itemId: Long,
    //PK
    name: String,
    description: String,
    imagePath: String,
    monthSales: Int,
    rating: Float,
    ratingCount: Int,
    satisfyCount: Int,
    satisfyRate: Float,
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends SnapshotDao

object FoodSnapshotDao {

  implicit def fromModel(model: Food)(implicit categoryDao: CategoryDao): FoodSnapshotDao = FoodSnapshotDao(
    restaurantId = categoryDao.restaurantId,
    categoryId = categoryDao.categoryId,
    itemId = model.itemId,
    name = model.name,
    description = model.description,
    imagePath = model.imagePath,
    monthSales = model.monthSales,
    rating = model.rating,
    ratingCount = model.ratingCount,
    satisfyCount = model.satisfyCount,
    satisfyRate = model.satisfyRate
  )
  implicit def fromModel(source: Seq[Food])(implicit converter: Food => FoodSnapshotDao, categoryDao: CategoryDao): Seq[FoodSnapshotDao] =
    source map converter

  implicit def toModel(dao: FoodSnapshotDao): Food = Food(
    itemId = dao.itemId,
    restaurantId = dao.restaurantId,
    categoryId = dao.categoryId,
    name = dao.name,
    description = dao.description,
    imagePath = dao.imagePath,
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
