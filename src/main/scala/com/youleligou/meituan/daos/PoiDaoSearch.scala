package com.youleligou.meituan.daos

import java.sql.Timestamp
import java.time.LocalDateTime

import com.youleligou.core.daos.Dao

/**
  * Created by liangliao on 8/5/17.
  */
case class PoiDaoSearch( // restaurant
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

object PoiDaoSearch {

  implicit def fromDao(dao: PoiDao): PoiDaoSearch = PoiDaoSearch(
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
    wmPoiViewId = dao.wmPoiViewId,
    createdAt = dao.createdAt
  )
  implicit def fromDao(source: Seq[PoiDao])(implicit converter: PoiDao => PoiDaoSearch): Seq[PoiDaoSearch] =
    source map converter

  implicit def toDao(search: PoiDaoSearch): PoiDao = PoiDao(
    id = search.id,
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
    latitude = search.latitude,
    longitude = search.longitude,
    shippingFeeTip = search.shippingFeeTip,
    minPriceTip = search.minPriceTip,
    wmPoiViewId = search.wmPoiViewId,
    createdAt = search.createdAt
  )
  implicit def toDao(source: Seq[PoiDaoSearch])(implicit converter: PoiDaoSearch => PoiDao): Seq[PoiDao] =
    source map converter

}
