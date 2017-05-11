package com.youleligou.baidu.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

/**
  * Created by liangliao on 4/5/17.
  */
//http://waimai.baidu.com/mobile/waimai?qt=shopmenu&&shop_id=9569582372677092679&display=json

case class Category(
    categoryId: Long,
    catalog: String,
    foods: Seq[Dish]
)


object Category {
  implicit val categoryReads: Reads[Category] = (
    (JsPath \ "category_id").read[Long] and
      (JsPath \ "catalog").read[String] and
      (JsPath \ "foods").read[Seq[Dish]]
    )(Category.apply _)
}
