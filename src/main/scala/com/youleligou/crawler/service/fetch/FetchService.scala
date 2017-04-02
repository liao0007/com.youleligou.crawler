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
  val ServiceUnavailable = 503
  val Timeout = 504
  val TooManyRequest = 429

  case class FetchException(statusCode: Int, message: String) extends Exception

}
