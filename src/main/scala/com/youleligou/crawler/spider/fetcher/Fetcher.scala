package com.youleligou.crawler.spider.fetcher

import com.youleligou.models.UrlInfo
import play.api.libs.ws.StandaloneWSRequest

import scala.concurrent.Future

/**
  * Created by liangliao on 31/3/17.
  */
trait Fetcher {
  def fetch(urlInfo: UrlInfo): Future[Option[StandaloneWSRequest#Response]]
}

object Fetcher {
  val Ok       = 200
  val NotFound = 404

  class FetchException(message: String, e: Throwable) extends Exception(message, e) {
    def this(message: String) = this(message, new Exception(message))
  }
}
