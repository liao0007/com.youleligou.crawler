package com.youleligou.eleme.daos.snapshot.search

import java.sql.{Date, Timestamp}
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.eleme.models.{Category, Food, Restaurant}

case class FoodSnapshotSearch(
    id: String,
    itemId: Long,
    name: String,
    restaurant: RestaurantSnapshotSearch,
    category: CategorySnapshotSearch,
    description: String,
    monthSales: Int,
    rating: Float,
    ratingCount: Int,
    satisfyCount: Int,
    satisfyRate: Float,
    createdDate: java.sql.Date = java.sql.Date.valueOf(LocalDate.now()),
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
)

object FoodSnapshotSearch {
  val createdDate: Date = java.sql.Date.valueOf(LocalDate.now())

  implicit def fromModel(model: Food)(implicit restaurantModel: Restaurant, categoryModel: Category): FoodSnapshotSearch =
    FoodSnapshotSearch(
      id = s"${model.itemId}-$createdDate",
      itemId = model.itemId,
      name = model.name,
      restaurant = restaurantModel,
      category = categoryModel,
      description = model.description,
      monthSales = model.monthSales,
      rating = model.rating,
      ratingCount = model.ratingCount,
      satisfyCount = model.satisfyCount,
      satisfyRate = model.satisfyRate,
      createdDate = createdDate
    )
  implicit def fromModel(source: Seq[Food])(implicit converter: Food => FoodSnapshotSearch,
                                            restaurantModel: Restaurant,
                                            categoryModel: Category): Seq[FoodSnapshotSearch] =
    source map converter

  implicit def toModel(dao: FoodSnapshotSearch): Food = Food(
    itemId = dao.itemId,
    name = dao.name,
    restaurantId = dao.restaurant.id,
    categoryId = dao.category.id,
    description = dao.description,
    monthSales = dao.monthSales,
    rating = dao.rating,
    ratingCount = dao.ratingCount,
    satisfyCount = dao.satisfyCount,
    satisfyRate = dao.satisfyRate
  )

  implicit def toModel(source: Seq[FoodSnapshotSearch])(implicit converter: FoodSnapshotSearch => Food): Seq[Food] =
    source map converter

}
