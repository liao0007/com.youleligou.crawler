package com.youleligou.crawler.service.fetch

import com.youleligou.crawler.model.{FetchResult, UrlInfo}

import scala.concurrent.Future

/**
  * Created by liangliao on 31/3/17.
  */
trait FetchService {
  def fetch(urlInfo: UrlInfo): Future[FetchResult]
}

object FetchService {
  val Ok = 200
  val NotFound = 404

  class FetchException(message: String, e: Throwable) extends Exception(message, e) {
    def this(message: String) = this(message, new Exception(message))
  }

}
