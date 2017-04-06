package com.youleligou.crawler.services

import com.youleligou.crawler.daos.CrawlerProxyServer
import com.youleligou.crawler.models.{FetchResult, UrlInfo}

import scala.concurrent.Future

/**
  * Created by liangliao on 31/3/17.
  */
trait FetchService {
  def fetch(jobName: String, urlInfo: UrlInfo, crawlerProxyServer: CrawlerProxyServer): Future[FetchResult]
}

object FetchService {
  val Ok = 200
  val PaymentRequired = 402
  val NotFound = 404
  val ServiceUnavailable = 503
  val Timeout = 504
  val TooManyRequest = 429

  case class FetchException(statusCode: Int, message: String) extends Exception

}