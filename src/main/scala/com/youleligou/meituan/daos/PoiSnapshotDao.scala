package com.youleligou.meituan.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao
import com.youleligou.meituan.modals.Poi

/**
  * Created by liangliao on 8/5/17.
  */
case class PoiSnapshotDao( // restaurant
                          id: Long,
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
                          latitude: Float,
                          longitude: Float,
                          shippingFeeTip: String,
                          minPriceTip: String,
                          wmPoiViewId: Long,
                          createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
                          createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now()))
    extends SnapshotDao

object PoiSnapshotDao {
  implicit def fromModel(model: Poi): PoiSnapshotDao = PoiSnapshotDao(
    id = model.id,
    mtPoiId = model.mtPoiId,
    name = model.name,
    status = model.status,
    picUrl = model.picUrl,
    avgDeliveryTime = model.avgDeliveryTime,
    shippingFee = model.shippingFee,
    minPrice = model.minPrice,
    monthSaleNum = model.monthSaleNum,
    brandType = model.brandType,
    sales = model.sales,
    wmPoiOpeningDays = model.wmPoiOpeningDays,
    latitude = model.latitude,
    longitude = model.longitude,
    shippingFeeTip = model.shippingFeeTip,
    minPriceTip = model.minPriceTip,
    wmPoiViewId = model.wmPoiViewId
  )
  implicit def fromModel(source: Seq[Poi])(implicit converter: Poi => PoiSnapshotDao): Seq[PoiSnapshotDao] =
    source map converter

  implicit def toModel(dao: PoiSnapshotDao): Poi = Poi(
    id = dao.id,
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
    latitude = dao.latitude,
    longitude = dao.longitude,
    shippingFeeTip = dao.shippingFeeTip,
    minPriceTip = dao.minPriceTip,
    wmPoiViewId = dao.wmPoiViewId
  )
  implicit def toModel(source: Seq[PoiSnapshotDao])(implicit converter: PoiSnapshotDao => Poi): Seq[Poi] =
    source map converter
}
