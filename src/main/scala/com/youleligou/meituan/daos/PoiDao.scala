package com.youleligou.meituan.daos

import java.sql.Timestamp
import java.time.LocalDateTime

import com.youleligou.core.daos.Dao
import com.youleligou.meituan.modals.Poi

/**
  * Created by liangliao on 8/5/17.
  */
case class PoiDao( // restaurant
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
                  latitude: Long,
                  longitude: Long,
                  shippingFeeTip: String,
                  minPriceTip: String,
                  wmPoiViewId: Long,
                  createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now()))
    extends Dao

object PoiDao {

  /*
  model <-> dao
   */
  implicit def fromModel(model: Poi): PoiDao = PoiDao(
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
  implicit def fromModel(source: Seq[Poi])(implicit converter: Poi => PoiDao): Seq[PoiDao] =
    source map converter

  implicit def toModel(dao: PoiDao): Poi = Poi(
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
  implicit def toModel(source: Seq[PoiDao])(implicit converter: PoiDao => Poi): Seq[Poi] =
    source map converter
}
