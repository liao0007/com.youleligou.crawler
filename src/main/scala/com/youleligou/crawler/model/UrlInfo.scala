package com.youleligou.crawler.model

import com.youleligou.crawler.model.UrlInfo.UrlType

/**
  * 爬取url类
  *
  * @param domain domain
  */
case class UrlInfo(domain: String, queryParameters: Map[String, String], urlType: UrlType, deep: Int) {
  val url: String = domain + queryParameters.map(queryParameter => queryParameter._1 + "=" + queryParameter._2).mkString("?", "&", "")

  override def toString: String = url
}

object UrlInfo {
  sealed trait UrlType

  case object SeedType extends UrlType
  case object GenerateType extends UrlType
}
