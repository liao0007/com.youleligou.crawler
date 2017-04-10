package com.youleligou.crawler.models

import play.api.libs.json.{Json, OFormat}

/**
  * Created by liangliao on 10/4/17.
  */
case class FetchRequest(requestName: String, urlInfo: UrlInfo, retry: Int = 0) {
  override def toString: String = urlInfo.toString
}

object FetchRequest {
  implicit val format: OFormat[FetchRequest] = Json.format[FetchRequest]
}
