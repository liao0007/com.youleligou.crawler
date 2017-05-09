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
                  brandType: Int,
                  latitude: Float,
                  longitude: Float,
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
    brandType = model.brandType,
    latitude = model.latitude,
    longitude = model.longitude,
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
    avgDeliveryTime = 0,
    shippingFee = 0f,
    minPrice = 0f,
    monthSaleNum = 0,
    brandType = dao.brandType,
    sales = 0,
    wmPoiOpeningDays = 0,
    latitude = dao.latitude,
    longitude = dao.longitude,
    shippingFeeTip = "",
    minPriceTip = "",
    wmPoiViewId = dao.wmPoiViewId
  )
  implicit def toModel(source: Seq[PoiDao])(implicit converter: PoiDao => Poi): Seq[Poi] =
    source map converter
}
