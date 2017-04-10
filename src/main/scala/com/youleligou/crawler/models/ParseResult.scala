package com.youleligou.crawler.models

case class ParseResult(title: Option[String] = None,
                       publishTime: Long = System.currentTimeMillis(),
                       updateTime: Long = System.currentTimeMillis(),
                       childLink: Seq[UrlInfo] = Seq.empty[UrlInfo],
                       fetchResponse: FetchResponse) {
  override def toString: String = "url=" + fetchResponse.fetchRequest.urlInfo.host + ",context length=" + fetchResponse.content.length
}
