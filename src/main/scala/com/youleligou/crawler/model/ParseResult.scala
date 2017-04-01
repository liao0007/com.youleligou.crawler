package com.youleligou.crawler.model

/**
  * 解析出来的HTTP网页信息
  */
case class ParseResult(
                        url: String,
                        title: Option[String] = None,
                        content: String,
                        publishTime: Long,
                        updateTime: Long,
                        childLink: List[UrlInfo] = List.empty[UrlInfo]
                      ) {
  override def toString: String = "url=" + url + ",context length=" + content.length
}
