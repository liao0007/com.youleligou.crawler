package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.Dao
import com.youleligou.eleme.models.FoodSku

case class FoodSkuSnapshotDao(
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
    createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends Dao

object FoodSkuSnapshotDao {

  implicit def fromModel(model: FoodSku): FoodSkuSnapshotDao = FoodSkuSnapshotDao(
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
  implicit def fromModel(source: Seq[FoodSku])(implicit converter: FoodSku => FoodSkuSnapshotDao): Seq[FoodSkuSnapshotDao] =
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
