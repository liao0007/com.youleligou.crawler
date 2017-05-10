package com.youleligou.meituan.daos

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao

/**
  * Created by liangliao on 8/5/17.
  */
case class PoiSnapshotDaoSearch( // restaurant
                                id: String,
                                poiId: Long,
                                mtPoiId: Long,
                                name: String,
                                status: Int,
                                picUrl: String,
                                avgDeliveryTime: Int,
                                shippingFee: Float,
                                minPrice: Float,
                                monthSaleNum: Int,
                                brandType: Int,
                                sales: Int,
                                wmPoiOpeningDays: Int,
                                location: Map[String, Float],
                                shippingFeeTip: String,
                                minPriceTip: String,
                                wmPoiViewId: Long,
                                createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
                                createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now()))
    extends SnapshotDao

object PoiSnapshotDaoSearch {
  implicit def fromDao(dao: PoiSnapshotDao): PoiSnapshotDaoSearch = {
    val formatter = new SimpleDateFormat("yyyy-MM-dd")
    PoiSnapshotDaoSearch(
      id = s"${dao.poiId}-${formatter.format(dao.createdDate)}",
      poiId = dao.poiId,
      mtPoiId = dao.mtPoiId,
      name = dao.name,
      status = dao.status,
      picUrl = dao.picUrl,
      avgDeliveryTime = dao.avgDeliveryTime,
      shippingFee = dao.shippingFee,
      minPrice = dao.minPrice,
      monthSaleNum = dao.monthSaleNum,
      brandType = dao.brandType,
      sales = dao.sales,
      wmPoiOpeningDays = dao.wmPoiOpeningDays,
      location = Map(
        "lat" -> dao.latitude,
        "lon" -> dao.longitude
      ),
      shippingFeeTip = dao.shippingFeeTip,
      minPriceTip = dao.minPriceTip,
      wmPoiViewId = dao.wmPoiViewId,
      createdDate = dao.createdDate,
      createdAt = dao.createdAt
    )
  }

  implicit def fromDao(source: Seq[PoiSnapshotDao])(implicit converter: PoiSnapshotDao => PoiSnapshotDaoSearch): Seq[PoiSnapshotDaoSearch] =
    source map converter

  implicit def toDao(search: PoiSnapshotDaoSearch): PoiSnapshotDao = PoiSnapshotDao(
    poiId = search.poiId,
    mtPoiId = search.mtPoiId,
    name = search.name,
    status = search.status,
    picUrl = search.picUrl,
    avgDeliveryTime = search.avgDeliveryTime,
    shippingFee = search.shippingFee,
    minPrice = search.minPrice,
    monthSaleNum = search.monthSaleNum,
    brandType = search.brandType,
    sales = search.sales,
    wmPoiOpeningDays = search.wmPoiOpeningDays,
    latitude = search.location("lat"),
    longitude = search.location("log"),
    shippingFeeTip = search.shippingFeeTip,
    minPriceTip = search.minPriceTip,
    wmPoiViewId = search.wmPoiViewId,
    createdDate = search.createdDate,
    createdAt = search.createdAt
  )
  implicit def toDao(source: Seq[PoiSnapshotDaoSearch])(implicit converter: PoiSnapshotDaoSearch => PoiSnapshotDao): Seq[PoiSnapshotDao] =
    source map converter

}
