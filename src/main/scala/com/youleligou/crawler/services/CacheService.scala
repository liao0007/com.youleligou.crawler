package com.youleligou.crawler.services

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractFetchActor.Fetch
import com.youleligou.crawler.actors.AbstractInjectActor.HashNxResult

import scala.concurrent.Future

/**
  * Created by dell on 2016/9/2.
  * 缓存接口
  */
trait CacheService extends LazyLogging {
  def hexists(key: String, field: String): Future[Boolean]

  def hsetnx(key: String, field: String, value: String, fetch: Fetch): Future[HashNxResult]

  def hlength(key: String): Future[Long]
}
