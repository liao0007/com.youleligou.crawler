package com.youleligou.baidu.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

/**
  * Created by liangliao on 11/5/17.
  */

//http://waimai.baidu.com/mobile/waimai?qt=shoplist&lat=4822314.36&lng=12965300.64

case class Shop(
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
    shopId: Long, //string to long
    saledMonth: Int,
    averageScore: Float,
    isNew: Boolean,
    shopLng: Float, // string to float div by 10e5
    shopLat: Float,
    isStore: Boolean,
    category: String,
    avgPrice: Float //String to float
)

object Shop {
  implicit val shopReads: Reads[Shop] = (
    (JsPath \ "average_service_score").read[Float] and
      (JsPath \ "comment_service_num").read[Int] and
      (JsPath \ "average_dish_score").read[Int] and
      (JsPath \ "comment_dish_num").read[Int] and
      (JsPath \ "saled").read[Int] and
      (JsPath \ "bd_express").read[Boolean] and
      (JsPath \ "shop_name").read[String] and
      (JsPath \ "shop_announcement").read[String] and
      (JsPath \ "logo_url").read[String] and
      (JsPath \ "brand").read[String] and
      (JsPath \ "bussiness_status").read[Int] and
      (JsPath \ "shop_id").read[String].map(_.toLong) and
      (JsPath \ "saled_month").read[Int] and
      (JsPath \ "average_score").read[Float] and
      (JsPath \ "is_new").read[Boolean] and
      (JsPath \ "shop_lng").read[String].map(_.toFloat / 10e5f) and
      (JsPath \ "shop_lat").read[String].map(_.toFloat / 10e5f) and
      (JsPath \ "is_store").read[Boolean] and
      (JsPath \ "category").read[String] and
      (JsPath \ "avg_price").read[String].map(_.toFloat)
  )(Shop.apply _)
}
