package com.youleligou.baidu.daos

import java.sql.Timestamp
import java.time.LocalDateTime

import com.youleligou.baidu.models.Shop
import com.youleligou.core.daos.Dao

case class ShopDao(
    shopId: Long,
    //PK
    bdExpress: Boolean,
    shopName: String,
    logoUrl: String,
    brand: String,
    bussinessStatus: Int,
    isNew: Boolean,
    shopLng: Float,
    shopLat: Float,
    isStore: Boolean,
    category: String,
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends Dao

object ShopDao {
  implicit def fromModel(model: Shop): ShopDao = ShopDao(
    shopId = model.shopId,
    //PK
    bdExpress = model.bdExpress,
    shopName = model.shopName,
    logoUrl = model.logoUrl,
    brand = model.brand,
    bussinessStatus = model.bussinessStatus,
    isNew = model.isNew,
    shopLng = model.shopLng,
    shopLat = model.shopLat,
    isStore = model.isStore,
    category = model.category
  )
  implicit def fromModel(source: Seq[Shop])(implicit converter: Shop => ShopDao): Seq[ShopDao] =
    source map converter

  implicit def toModel(dao: ShopDao): Shop = Shop(
    averageServiceScore = 0f,
    commentServiceNum = 0,
    averageDishScore = 0,
    commentDishNum = 0,
    saled = 0,
    bdExpress = dao.bdExpress,
    shopName = dao.shopName,
    shopAnnouncement = "",
    logoUrl = dao.logoUrl,
    brand = dao.brand,
    bussinessStatus = dao.bussinessStatus,
    shopId = dao.shopId,
    saledMonth = 0,
    averageScore = 0f,
    isNew = dao.isNew,
    shopLng = dao.shopLng,
    shopLat = dao.shopLat,
    isStore = dao.isStore,
    category = dao.category,
    avgPrice = 0f
  )
  implicit def toModel(source: Seq[ShopDao])(implicit converter: ShopDao => Shop): Seq[Shop] =
    source map converter

  implicit def fromSnapshot(snapshot: ShopSnapshotDao): ShopDao = ShopDao(
    shopId = snapshot.shopId,
    //PK
    bdExpress = snapshot.bdExpress,
    shopName = snapshot.shopName,
    logoUrl = snapshot.logoUrl,
    brand = snapshot.brand,
    bussinessStatus = snapshot.bussinessStatus,
    isNew = snapshot.isNew,
    shopLng = snapshot.shopLng,
    shopLat = snapshot.shopLat,
    isStore = snapshot.isStore,
    category = snapshot.category
  )
  implicit def fromSnapshot(source: Seq[ShopSnapshotDao])(implicit converter: ShopSnapshotDao => ShopDao): Seq[ShopDao] =
    source map converter
}
