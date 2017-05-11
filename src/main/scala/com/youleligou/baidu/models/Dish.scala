package com.youleligou.baidu.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

/**
  * Created by liangliao on 23/4/17.
  */
case class Dish(
    categoryId: Long, //string to long
    itemId: Long, //String to long
    name: String,
    url: String,
    purchaseLimit: Int,
    originPrice: Float, //string to float
    currentPrice: Float, //string to float
    saledOut: Boolean, //string to Boolean
    saled: Int,
    description: String,
    onSale: Boolean,
    recommendNum: Int,
    goodCommentNum: Int,
    badCommentNum: Int,
    totalCommentNum: Int,
    goodCommentRatio: Float, //string to float
    haveAttr: Boolean,
    dishType: Int,
    dishAttr: Seq[DishAttribute] = Seq.empty[DishAttribute]
)

object Dish {
  implicit val shopReads: Reads[Dish] = (
    (JsPath \ "category_id").read[String].map(_.toLong) and
      (JsPath \ "item_id").read[String].map(_.toLong) and
      (JsPath \ "name").read[String] and
      (JsPath \ "url").read[String] and
      (JsPath \ "purchase_limit").read[Int] and
      (JsPath \ "origin_price").read[String].map(_.toFloat) and
      (JsPath \ "current_price").read[String].map(_.toFloat) and
      (JsPath \ "saled_out").read[String].map(_.toBoolean) and
      (JsPath \ "saled").read[Int] and
      (JsPath \ "description").read[String] and
      (JsPath \ "on_sale").read[Boolean] and
      (JsPath \ "recommend_num").read[String].map(_.toInt) and
      (JsPath \ "good_comment_num").read[Int] and
      (JsPath \ "bad_comment_num").read[Int] and
      (JsPath \ "total_comment_num").read[Int] and
      (JsPath \ "good_comment_ratio").read[String].map(_.toFloat) and
      (JsPath \ "have_attr").read[Boolean] and
      (JsPath \ "dish_type").read[Int] and
      (JsPath \ "dish_attr").read[Seq[DishAttribute]]
  )(Dish.apply _)
}
