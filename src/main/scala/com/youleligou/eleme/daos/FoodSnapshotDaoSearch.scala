package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao
import com.youleligou.eleme.models.FoodSku

import scala.util.Try

case class FoodSnapshotDaoSearch(
    id: String,
    itemId: Long,
    name: String,
    description: String,
    monthRevenue: Float,
    monthSales: Int,
    balancedPrice: Float,
    rating: Float,
    ratingCount: Int,
    satisfyCount: Int,
    satisfyRate: Float,
    restaurant: RestaurantDaoSearch,
    category: CategoryDaoSearch,
    foodSkus: Seq[FoodSku],
    createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends SnapshotDao

object FoodSnapshotDaoSearch {
  implicit def fromDao(
      dao: FoodSnapshotDao)(implicit restaurant: RestaurantDaoSearch, category: CategoryDaoSearch, foodSkus: Seq[FoodSku]): FoodSnapshotDaoSearch = {
    val balancedPrice: Float =
      Try(foodSkus.map(foodSku => foodSku.price * foodSku.recentPopularity).sum / foodSkus.map(_.recentPopularity).sum).getOrElse(0f)
    val formatter = new SimpleDateFormat("yyyy-MM-dd")

    FoodSnapshotDaoSearch(
      id = s"${dao.itemId}-${formatter.format(dao.createdDate)}",
      itemId = dao.itemId,
      name = dao.name,
      description = dao.description,
      monthRevenue = dao.monthSales * balancedPrice,
      monthSales = dao.monthSales,
      balancedPrice = balancedPrice,
      rating = dao.rating,
      ratingCount = dao.ratingCount,
      satisfyCount = dao.satisfyCount,
      satisfyRate = dao.satisfyRate,
      restaurant = restaurant,
      category = category,
      foodSkus = foodSkus,
      createdDate = dao.createdDate,
      createdAt = dao.createdAt
    )
  }

  implicit def toDao(search: FoodSnapshotDaoSearch): FoodSnapshotDao = FoodSnapshotDao(
    itemId = search.itemId,
    name = search.name,
    restaurantId = search.restaurant.id,
    categoryId = search.category.id,
    description = search.description,
    monthSales = search.monthSales,
    rating = search.rating,
    ratingCount = search.ratingCount,
    satisfyCount = search.satisfyCount,
    satisfyRate = search.satisfyRate,
    createdDate = search.createdDate,
    createdAt = search.createdAt
  )
  implicit def toDao(source: Seq[FoodSnapshotDaoSearch])(implicit converter: FoodSnapshotDaoSearch => FoodSnapshotDao): Seq[FoodSnapshotDao] =
    source map converter

}
