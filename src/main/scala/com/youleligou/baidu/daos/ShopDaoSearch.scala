package com.youleligou.baidu.daos

import java.sql.Timestamp
import java.time.LocalDateTime

import com.youleligou.core.daos.Dao

case class ShopDaoSearch(
    shopId: Long,
    //PK
    bdExpress: Boolean,
    shopName: String,
    logoUrl: String,
    brand: String,
    bussinessStatus: Int,
    isNew: Boolean,
    location: Map[String, Float],
    isStore: Boolean,
    category: String,
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends Dao

object ShopDaoSearch {

  implicit def fromDao(dao: ShopDao): ShopDaoSearch = ShopDaoSearch(
    shopId = dao.shopId,
    //PK
    bdExpress = dao.bdExpress,
    shopName = dao.shopName,
    logoUrl = dao.logoUrl,
    brand = dao.brand,
    bussinessStatus = dao.bussinessStatus,
    isNew = dao.isNew,
    isStore = dao.isStore,
    category = dao.category,
    location = Map(
      "lat" -> dao.shopLat,
      "lon" -> dao.shopLng
    ),
    createdAt = dao.createdAt
  )
  implicit def fromDao(source: Seq[ShopDao])(implicit converter: ShopDao => ShopDaoSearch): Seq[ShopDaoSearch] =
    source map converter

  implicit def toDao(search: ShopDaoSearch): ShopDao = ShopDao(
    shopId = search.shopId,
    //PK
    bdExpress = search.bdExpress,
    shopName = search.shopName,
    logoUrl = search.logoUrl,
    brand = search.brand,
    bussinessStatus = search.bussinessStatus,
    isNew = search.isNew,
    shopLng = search.location("log"),
    shopLat = search.location("lat"),
    isStore = search.isStore,
    category = search.category,
    createdAt = search.createdAt
  )
  implicit def toDao(source: Seq[ShopDaoSearch])(implicit converter: ShopDaoSearch => ShopDao): Seq[ShopDao] =
    source map converter

  implicit def fromSnapshotDao(dao: ShopSnapshotDao): ShopDaoSearch = ShopDaoSearch(
    shopId = dao.shopId,
    //PK
    bdExpress = dao.bdExpress,
    shopName = dao.shopName,
    logoUrl = dao.logoUrl,
    brand = dao.brand,
    bussinessStatus = dao.bussinessStatus,
    isNew = dao.isNew,
    isStore = dao.isStore,
    category = dao.category,
    location = Map(
      "lat" -> dao.shopLat,
      "lon" -> dao.shopLng
    ),
    createdAt = dao.createdAt
  )
  implicit def fromSnapshotDao(source: Seq[ShopSnapshotDao])(implicit converter: ShopSnapshotDao => ShopDaoSearch): Seq[ShopDaoSearch] =
    source map converter
}
