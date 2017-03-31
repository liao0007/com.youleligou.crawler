package com.youleligou.crawler.models

import com.youleligou.crawler.models.UrlInfo.UrlType

/**
  * 爬取url类
  *
  * @param url    url
  * @param parent 父url
  */
case class UrlInfo(url: String, parent: Option[String], urlType: UrlType, deep: Int) {
  override def toString: String = url + "\n"
}

object UrlInfo {
  sealed trait UrlType
  case object SeedType extends UrlType
  case object GenerateType extends UrlType
}




