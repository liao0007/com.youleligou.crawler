package com.youleligou.crawler.models

import com.youleligou.crawler.models.UrlInfo.UrlType
import com.youleligou.crawler.models.UrlInfo.UrlType.UrlType

/**
  * 爬取url类
  *
  * @param host domain
  */
case class UrlInfo(host: String, queryParameters: Map[String, String] = Map.empty[String, String], urlType: UrlType = UrlType.Generated, deep: Int = 0) {
  val url: String               = host + queryParameters.map(queryParameter => queryParameter._1 + "=" + queryParameter._2).mkString("?", "&", "")
  override def toString: String = url
}

object UrlInfo {

  object UrlType {
    sealed trait UrlType
    case object Seed      extends UrlType
    case object Generated extends UrlType
  }

}
