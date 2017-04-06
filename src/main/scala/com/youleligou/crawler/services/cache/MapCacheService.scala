package com.youleligou.crawler.services.cache

import com.youleligou.crawler.services.CacheService

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

/**
  * 采用本地Map实现的缓存
  */
class MapCacheService extends CacheService {

  private val map = new mutable.HashMap[String, mutable.HashMap[String, String]]()

  def hexists(key: String, field: String): Future[Boolean] = Future.successful {
    map.contains(key) && map(key).contains(field)
  }

  def hget(key: String, field: String): Future[Option[String]] = Future.successful {
    map.get(key) flatMap { innerMap =>
      innerMap.get(field)
    }
  }

  def hset(key: String, field: String, value: String): Future[Boolean] =
    hexists(key, field) map {
      case true =>
        map(key) += (field -> value)
        true
      case _ =>
        map(key) = mutable.HashMap(field -> value)
        true

    }

  def hlength(key: String): Future[Long] = Future.successful {
    map.get(key) map { innerMap =>
      innerMap.size.toLong
    } getOrElse 0L
  }
}
