package com.youleligou.crawler.caches

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

/**
  * 采用本地Map实现的缓存
  */
class MapCache extends Cache {

  private val map = new mutable.HashMap[String, String]()

  def contains(key: String): Future[Boolean] = Future.successful(map.contains(key))

  def get(key: String): uture[Option[String]] = Future.successful(map.get(key))

  def put(key: String, value: String): Future[Boolean] = Future.successful {
    map.put(key, value)
    true
  }

  def size(): Future[Long] = Future.successful(map.size.toLong)

  def keys(): Future[scala.collection.Set[String]] = Future.successful {
    map.keySet
  }
}
