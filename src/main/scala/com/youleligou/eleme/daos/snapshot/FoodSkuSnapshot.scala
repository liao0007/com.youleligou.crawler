package com.youleligou.eleme.daos.snapshot

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.eleme.daos.snapshot.search.FoodSnapshotSearch
import com.youleligou.eleme.models.{Category, FoodSku, Restaurant}

case class FoodSkuSnapshot(
    originalPrice: Option[Float],
    skuId: Long,
    name: String,
    restaurantId: Long,
    foodId: Long,
    packingFee: Float,
    recentRating: Float,
    promotionStock: Int,
    price: Float,
    soldOut: Boolean,
    recentPopularity: Int,
    isEssential: Boolean,
    itemId: Long,
    checkoutMode: Int,
    stock: Int,
    createdDate: java.sql.Date = java.sql.Date.valueOf(LocalDate.now()),
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
)

object FoodSkuSnapshot {

  /*
  model <-> dao
   */
  implicit def fromModel(model: FoodSkuSnapshot): FoodSkuSnapshot = FoodSkuSnapshot(
    originalPrice = model.originalPrice,
    skuId = model.skuId,
    name = model.name,
    restaurantId = model.restaurantId,
    foodId = model.foodId,
    packingFee = model.packingFee,
    recentRating = model.recentRating,
    promotionStock = model.promotionStock,
    price = model.price,
    soldOut = model.soldOut,
    recentPopularity = model.recentPopularity,
    isEssential = model.isEssential,
    itemId = model.itemId,
    checkoutMode = model.checkoutMode,
    stock = model.stock
  )

  implicit def fromModel(source: Seq[FoodSkuSnapshot])(implicit converter: FoodSkuSnapshot => FoodSkuSnapshot): Seq[FoodSkuSnapshot] =
    source map converter

  implicit def toModel(dao: FoodSkuSnapshot): FoodSkuSnapshot = FoodSkuSnapshot(
    originalPrice = dao.originalPrice,
    skuId = dao.skuId,
    name = dao.name,
    restaurantId = dao.restaurantId,
    foodId = dao.foodId,
    packingFee = dao.packingFee,
    recentRating = dao.recentRating,
    promotionStock = dao.promotionStock,
    price = dao.price,
    soldOut = dao.soldOut,
    recentPopularity = dao.recentPopularity,
    isEssential = dao.isEssential,
    itemId = dao.itemId,
    checkoutMode = dao.checkoutMode,
    stock = dao.stock
  )

  implicit def toModel(source: Seq[FoodSkuSnapshot])(implicit converter: FoodSkuSnapshot => FoodSkuSnapshot): Seq[FoodSkuSnapshot] =
    source map converter

  /*
  search <-> dao
   */
  implicit def fromSearch(search: FoodSkuSnapshotSearch)(implicit restaurantModel: RestaurantSnapshot, categoryModel: Category): FoodSkuSnapshot = {
    FoodSkuSnapshot(
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

  implicit def fromSearch(source: Seq[FoodSnapshot])(implicit converter: FoodSnapshot => FoodSkuSnapshot): Seq[FoodSkuSnapshot] =
    source map converter

  implicit def toSearch(dao: FoodSkuSnapshot)(implicit restaurantModel: RestaurantSnapshot, categoryModel: Category): FoodSnapshot =
    FoodSnapshot(
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

  implicit def toSearch(source: Seq[FoodSkuSnapshot])(implicit converter: FoodSkuSnapshot => FoodSnapshot): Seq[FoodSnapshot] =
    source map converter

}
