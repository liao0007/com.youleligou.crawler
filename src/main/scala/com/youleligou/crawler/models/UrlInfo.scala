package com.youleligou.crawler.models

import com.youleligou.crawler.models.UrlInfo.{UrlInfoType, UrlType}
import play.api.libs.json.Json

/**
  * 爬取url类
  *
  * @param host domain
  */
case class UrlInfo(host: String,
                   queryParameters: Map[String, String] = Map.empty[String, String],
                   urlType: String = UrlInfoType.Generated,
                   deep: Int = 0) {
  val url: String               = host + queryParameters.map(queryParameter => queryParameter._1 + "=" + queryParameter._2).mkString("?", "&", "")
  override def toString: String = url
}

object UrlInfo {

  implicit val format = Json.format[UrlInfo]

  sealed trait UrlType
  object UrlInfoType {
    case object Seed      extends UrlType
    case object Generated extends UrlType
    implicit def fromString(x: String): Option[UrlType] = Seq(Seed, Generated).find(_.toString == x)
    implicit def toString(x: UrlType): String           = x.toString
  }

}
