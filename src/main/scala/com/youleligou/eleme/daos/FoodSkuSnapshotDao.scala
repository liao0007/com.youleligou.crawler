package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao
import com.youleligou.eleme.models.{Food, FoodSku}

case class FoodSkuSnapshotDao(
    createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
    restaurantId: Long,
    categoryId: Long,
    itemId: Long,
    skuId: Long,
    //PK
    originalPrice: Option[Float],
    name: String,
    foodId: Long,
    packingFee: Float,
    recentRating: Float,
    promotionStock: Int,
    price: Float,
    soldOut: Boolean,
    recentPopularity: Int,
    isEssential: Boolean,
    checkoutMode: Int,
    stock: Int,
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends SnapshotDao

object FoodSkuSnapshotDao {

  implicit def fromModel(model: FoodSku)(implicit foodModel: Food): FoodSkuSnapshotDao = FoodSkuSnapshotDao(
    restaurantId = foodModel.restaurantId,
    categoryId = foodModel.categoryId,
    itemId = model.itemId,
    skuId = model.skuId,
    originalPrice = model.originalPrice,
    name = model.name,
    foodId = model.foodId,
    packingFee = model.packingFee,
    recentRating = model.recentRating,
    promotionStock = model.promotionStock,
    price = model.price,
    soldOut = model.soldOut,
    recentPopularity = model.recentPopularity,
    isEssential = model.isEssential,
    checkoutMode = model.checkoutMode,
    stock = model.stock
  )
  implicit def fromModel(source: Seq[FoodSku])(implicit converter: FoodSku => FoodSkuSnapshotDao, foodModel: Food): Seq[FoodSkuSnapshotDao] =
    source map converter

  implicit def toModel(dao: FoodSkuSnapshotDao): FoodSku = FoodSku(
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
  implicit def toModel(source: Seq[FoodSkuSnapshotDao])(implicit converter: FoodSkuSnapshotDao => FoodSku): Seq[FoodSku] =
    source map converter

}
