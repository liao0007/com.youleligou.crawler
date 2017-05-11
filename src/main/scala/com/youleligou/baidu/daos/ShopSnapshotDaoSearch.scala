package com.youleligou.baidu.daos

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.{LocalDate, LocalDateTime}
import java.util.Date

import com.youleligou.core.daos.SnapshotDao

case class ShopSnapshotDaoSearch(
    id: String,
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
    location: Map[String, Float],
    isStore: Boolean,
    category: String,
    avgPrice: Float,
    createdAt: Date = Timestamp.valueOf(LocalDateTime.now())
) extends SnapshotDao

object ShopSnapshotDaoSearch {

  implicit def fromDao(dao: ShopSnapshotDao): ShopSnapshotDaoSearch = {
    val formatter = new SimpleDateFormat("yyyy-MM-dd")
    ShopSnapshotDaoSearch(
      id = s"${dao.shopId}-${formatter.format(dao.createdDate)}",
      createdDate = dao.createdDate,
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
      location = Map(
        "lat" -> dao.shopLat,
        "lon" -> dao.shopLng
      ),
      isStore = dao.isStore,
      category = dao.category,
      avgPrice = dao.avgPrice,
      createdAt = dao.createdAt
    )
  }
  implicit def fromDao(source: Seq[ShopSnapshotDao])(implicit converter: ShopSnapshotDao => ShopSnapshotDaoSearch): Seq[ShopSnapshotDaoSearch] =
    source map converter

  implicit def toDao(search: ShopSnapshotDaoSearch): ShopSnapshotDao = ShopSnapshotDao(
    createdDate = search.createdDate,
    shopId = search.shopId,
    //PK
    averageServiceScore = search.averageServiceScore,
    commentServiceNum = search.commentServiceNum,
    averageDishScore = search.averageDishScore,
    commentDishNum = search.commentDishNum,
    saled = search.saled,
    bdExpress = search.bdExpress,
    shopName = search.shopName,
    shopAnnouncement = search.shopAnnouncement,
    logoUrl = search.logoUrl,
    brand = search.brand,
    bussinessStatus = search.bussinessStatus,
    saledMonth = search.saledMonth,
    averageScore = search.averageScore,
    isNew = search.isNew,
    isStore = search.isStore,
    category = search.category,
    avgPrice = search.avgPrice,
    createdAt = search.createdAt,
    shopLat = search.location("lat"),
    shopLng = search.location("log")
  )
  implicit def toDao(source: Seq[ShopSnapshotDaoSearch])(implicit converter: ShopSnapshotDaoSearch => ShopSnapshotDao): Seq[ShopSnapshotDao] =
    source map converter

}
