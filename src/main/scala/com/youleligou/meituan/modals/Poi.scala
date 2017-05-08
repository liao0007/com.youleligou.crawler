package com.youleligou.meituan.modals

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

/**
  * Created by liangliao on 8/5/17.
  */
case class Poi( // restaurant
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
               wmPoiViewId: Long)

object Poi {
  implicit val poiReads: Reads[Poi] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "mt_poi_id").read[Long] and
      (JsPath \ "name").read[String] and
      (JsPath \ "status").read[Int] and
      (JsPath \ "pic_url").read[String] and
      (JsPath \ "avg_delivery_time").read[Int] and
      (JsPath \ "shipping_fee").read[Float] and
      (JsPath \ "min_price").read[Float] and
      (JsPath \ "month_sale_num").read[Int] and
      (JsPath \ "brand_type").read[Int] and
      (JsPath \ "sales").read[Int] and
      (JsPath \ "wm_poi_opening_days").read[Int] and
      (JsPath \ "latitude").read[Long] and
      (JsPath \ "longitude").read[Long] and
      (JsPath \ "shipping_fee_tip").read[String] and
      (JsPath \ "min_price_tip").read[String] and
      (JsPath \ "wm_poi_view_id").read[String].map(_.toLong)
  )(Poi.apply _)
}
