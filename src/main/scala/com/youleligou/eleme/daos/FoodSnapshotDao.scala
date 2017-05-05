package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.eleme.models.{Category, Food, Identification, Restaurant}

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
)

object FoodSnapshotDao {

  /*
  model <-> dao
   */
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
    satisfyRate = dao.satisfyRate
  )

  implicit def toModel(source: Seq[FoodSnapshotDao])(implicit converter: FoodSnapshotDao => Food): Seq[Food] =
    source map converter

  /*
  search <-> dao
   */
  implicit def fromSearch(search: FoodSnapshotSearch)(implicit restaurantModel: Restaurant, categoryModel: Category): FoodSnapshotDao = {
    FoodSnapshotDao(
      itemId = search.itemId,
      restaurantId = search.restaurantId,
      categoryId = search.categoryId,
      name = search.name,
      description = search.description,
      monthSales = search.monthSales,
      rating = search.rating,
      ratingCount = search.ratingCount,
      satisfyCount = search.satisfyCount,
      satisfyRate = search.satisfyRate,
      createdDate = search.createdDate,
      createdAt = search.createdAt
    )
  }

  implicit def fromSearch(source: Seq[FoodSnapshotSearch])(implicit converter: FoodSnapshotSearch => FoodSnapshotDao): Seq[FoodSnapshotDao] =
    source map converter

  implicit def toSearch(dao: FoodSnapshotDao)(implicit restaurantModel: Restaurant, categoryModel: Category): FoodSnapshotSearch =
    FoodSnapshotSearch(
      id = s"${dao.itemId}-${dao.createdDate}",
      itemId = dao.itemId,
      name = dao.name,
      restaurant = restaurantModel,
      category = categoryModel,
      description = dao.description,
      monthSales = dao.monthSales,
      rating = dao.rating,
      ratingCount = dao.ratingCount,
      satisfyCount = dao.satisfyCount,
      satisfyRate = dao.satisfyRate,
      createdDate = dao.createdDate
    )

  implicit def toSearch(source: Seq[FoodSnapshotDao])(implicit converter: FoodSnapshotDao => FoodSnapshotSearch): Seq[FoodSnapshotSearch] =
    source map converter


}
