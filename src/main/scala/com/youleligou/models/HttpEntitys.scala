package com.youleligou.models

import com.youleligou.models.UrlInfo.UrlType

/**
  * Created by young.yang on 2016/8/28.
  * 通过爬取回来的http原始页面
  */
case class HttpResult(status: Int, content: String, message: String, url: String, deep: Int) {
  override def toString() = "status=" + status + ",context length=" + content.length + ",url=" + url
}

/**
  * 爬取url类
  *
  * @param url    url
  * @param parent 父url
  */
case class UrlInfo(url: String, parent: String, urlType: UrlType, deep: Int) {
  override def toString() = url + "\n"
}
object UrlInfo {
  sealed trait UrlType
  case object SeedType     extends UrlType
  case object GenerateType extends UrlType
}

/**
  * 种子类
  *
  * @param url 种子url
  */
case class Seed(url: String) {
  override def toString() = url + "\n"
}

/**
  * 解析出来的HTTP网页信息
  */
case class HttpPage(
    url: String,
    title: Option[String] = None,
    html: Option[String] = None,
    content: String,
    publishTime: Option[Long] = None,
    updateTime: Option[Long] = None,
    author: Option[String] = None,
    keywords: Option[String] = None,
    desc: Option[String] = None,
    childLink: (List[UrlInfo], Int) = (List.empty[UrlInfo], 0),
    meta: Map[String, String] = Map.empty[String, String]
) {
  override def toString(): String = "url=" + url + ",context length=" + content.length
}
