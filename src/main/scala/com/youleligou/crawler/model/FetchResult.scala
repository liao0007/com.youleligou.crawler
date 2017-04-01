package com.youleligou.crawler.model

/**
  * Created by young.yang on 2016/8/28.
  * 通过爬取回来的http原始页面
  */
case class FetchResult(status: Int, content: String, message: String, urlInfo: UrlInfo) {
  override def toString: String = "status=" + status + ",context length=" + content.length + ",url=" + urlInfo
}
