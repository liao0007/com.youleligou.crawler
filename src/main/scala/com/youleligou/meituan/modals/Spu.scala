package com.youleligou.meituan.modals

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

/**
  * Created by liangliao on 8/5/17.
  */
case class Spu( // food
                id: Long,
                name: String,
                minPrice: Float,
                praiseNum: Int,
                treadNum: Int,
                praiseNumNew: Int,
                description: Option[String],
                picture: String,
                monthSaled: Int,
                status: Int,
                tag: Long,
                skus: Seq[Sku]
)

object Spu {
  implicit val poiReads: Reads[Spu] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "name").read[String] and
      (JsPath \ "min_price").read[Float] and
      (JsPath \ "praise_num").read[Int] and
      (JsPath \ "tread_num").read[Int] and
      (JsPath \ "praise_num_new").read[Int] and
      (JsPath \ "description").readNullable[String] and
      (JsPath \ "picture").read[String] and
      (JsPath \ "month_saled").read[Int] and
      (JsPath \ "status").read[Int] and
      (JsPath \ "tag").read[String].map(_.toLong) and
      (JsPath \ "skus").read[Seq[Sku]]
  )(Spu.apply _)
}
