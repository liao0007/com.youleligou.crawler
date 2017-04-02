package com.youleligou.crawler.service.cache

import com.youleligou.crawler.service.CacheService

import scala.collection.mutable
import scala.concurrent.Future

/**
  * 采用本地Map实现的缓存
  */
class MapCacheService extends CacheService {

  private val map = new mutable.HashMap[String, String]()

  def contains(key: String): Future[Boolean] = Future.successful(map.contains(key))

  def get(key: String): Future[Option[String]] = Future.successful(map.get(key))

  def put(key: String, value: String): Future[Boolean] = Future.successful {
    map.put(key, value)
    true
  }

  def size(): Future[Long] = Future.successful(map.size.toLong)
}
