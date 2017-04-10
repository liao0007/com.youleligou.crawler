package com.youleligou.crawler.models

/**
  * Created by liangliao on 10/4/17.
  */
case class FetchResponse(status: Int, content: String, message: String, fetchRequest: FetchRequest) {
  override def toString: String = "status=" + status + ",context length=" + content.length + ",url=" + fetchRequest.urlInfo
}
