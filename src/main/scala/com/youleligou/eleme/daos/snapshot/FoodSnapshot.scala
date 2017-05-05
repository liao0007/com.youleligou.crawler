package com.youleligou.eleme.daos.snapshot

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.eleme.daos.snapshot.search.FoodSnapshotSearch
import com.youleligou.eleme.models.{Category, Food, Restaurant}

case class FoodSnapshot(
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

object FoodSnapshot {

  /*
  model <-> dao
   */
  implicit def fromModel(model: Food): FoodSnapshot = FoodSnapshot(
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
  implicit def fromModel(source: Seq[Food])(implicit converter: Food => FoodSnapshot): Seq[FoodSnapshot] =
    source map converter

  implicit def toModel(dao: FoodSnapshot): Food = Food(
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
  implicit def toModel(source: Seq[FoodSnapshot])(implicit converter: FoodSnapshot => Food): Seq[Food] =
    source map converter

  /*
  search <-> dao
   */
  implicit def fromSearch(search: FoodSnapshotSearch)(implicit restaurantModel: Restaurant, categoryModel: Category): FoodSnapshot = {
    FoodSnapshot(
      itemId = search.itemId,
      restaurantId = search.restaurant.id,
      categoryId = search.category.id,
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

  implicit def fromSearch(source: Seq[FoodSnapshotSearch])(implicit converter: FoodSnapshotSearch => FoodSnapshot): Seq[FoodSnapshot] =
    source map converter

  implicit def toSearch(dao: FoodSnapshot)(implicit restaurantSnapshot: RestaurantSnapshot, categorySnapshot: CategorySnapshot): FoodSnapshotSearch =
    FoodSnapshotSearch(
      id = s"${dao.itemId}-${dao.createdDate}",
      itemId = dao.itemId,
      name = dao.name,
      restaurant = restaurantSnapshot,
      category = categorySnapshot,
      description = dao.description,
      monthSales = dao.monthSales,
      rating = dao.rating,
      ratingCount = dao.ratingCount,
      satisfyCount = dao.satisfyCount,
      satisfyRate = dao.satisfyRate,
      createdDate = dao.createdDate
    )

  implicit def toSearch(source: Seq[FoodSnapshot])(implicit converter: FoodSnapshot => FoodSnapshotSearch): Seq[FoodSnapshotSearch] =
    source map converter

}
