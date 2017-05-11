package com.youleligou.baidu.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}
import java.util.Date

import com.youleligou.baidu.models.Shop
import com.youleligou.core.daos.SnapshotDao

case class ShopSnapshotDao(
    createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
    shopId: Long,
    //PK
    averageServiceScore: Float,
    commentServiceNum: Int,
    averageDishScore: Int,
    commentDishNum: Int,
    saled: Int,
    bdExpress: Boolean,
    shopName: String,
    shopAnnouncement: String,
    logoUrl: String,
    brand: String,
    bussinessStatus: Int,
    saledMonth: Int,
    averageScore: Float,
    isNew: Boolean,
    shopLng: Float,
    shopLat: Float,
    isStore: Boolean,
    category: String,
    avgPrice: Float,
    createdAt: Date = Timestamp.valueOf(LocalDateTime.now())
) extends SnapshotDao

object ShopSnapshotDao {

  /*
  model <-> dao
   */
  implicit def fromModel(model: Shop): ShopSnapshotDao = ShopSnapshotDao(
    shopId = model.shopId,
    //PK
    averageServiceScore = model.averageServiceScore,
    commentServiceNum = model.commentServiceNum,
    averageDishScore = model.averageDishScore,
    commentDishNum = model.commentDishNum,
    saled = model.saled,
    bdExpress = model.bdExpress,
    shopName = model.shopName,
    shopAnnouncement = model.shopAnnouncement,
    logoUrl = model.logoUrl,
    brand = model.brand,
    bussinessStatus = model.bussinessStatus,
    saledMonth = model.saledMonth,
    averageScore = model.averageScore,
    isNew = model.isNew,
    shopLng = model.shopLng,
    shopLat = model.shopLat,
    isStore = model.isStore,
    category = model.category,
    avgPrice = model.avgPrice
  )

  implicit def fromModel(source: Seq[Shop])(implicit converter: Shop => ShopSnapshotDao): Seq[ShopSnapshotDao] =
    source map converter

  implicit def toModel(dao: ShopSnapshotDao): Shop = Shop(
    shopId = dao.shopId,
    //PK
    averageServiceScore = dao.averageServiceScore,
    commentServiceNum = dao.commentServiceNum,
    averageDishScore = dao.averageDishScore,
    commentDishNum = dao.commentDishNum,
    saled = dao.saled,
    bdExpress = dao.bdExpress,
    shopName = dao.shopName,
    shopAnnouncement = dao.shopAnnouncement,
    logoUrl = dao.logoUrl,
    brand = dao.brand,
    bussinessStatus = dao.bussinessStatus,
    saledMonth = dao.saledMonth,
    averageScore = dao.averageScore,
    isNew = dao.isNew,
    shopLng = dao.shopLng,
    shopLat = dao.shopLat,
    isStore = dao.isStore,
    category = dao.category,
    avgPrice = dao.avgPrice
  )
  implicit def toModel(source: Seq[ShopSnapshotDao])(implicit converter: ShopSnapshotDao => Shop): Seq[Shop] =
    source map converter

}
