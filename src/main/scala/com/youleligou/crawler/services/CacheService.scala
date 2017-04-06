package com.youleligou.crawler.services

import scala.concurrent.Future

/**
  * Created by dell on 2016/9/2.
  * 缓存接口
  */
trait CacheService {
  def hexists(key: String, field: String): Future[Boolean]

  def hset(key: String, field: String, value: String): Future[Boolean]

  def hget(key: String, field: String): Future[Option[String]]

  def hlength(key: String): Future[Long]
}
