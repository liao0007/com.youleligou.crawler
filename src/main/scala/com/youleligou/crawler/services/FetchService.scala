package com.youleligou.crawler.services

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.daos.CrawlerProxyServer
import com.youleligou.crawler.models.{FetchRequest, FetchResponse}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by liangliao on 31/3/17.
  */
trait FetchService extends LazyLogging {
  def fetch(fetchRequest: FetchRequest, crawlerProxyServerOpt: Option[CrawlerProxyServer])(implicit executor: ExecutionContext): Future[FetchResponse]
}

object FetchService {
  val Ok                 = 200
  val PaymentRequired    = 402
  val NotFound           = 404
  val ServiceUnavailable = 503
  val Timeout            = 504
  val TooManyRequest     = 429
}
