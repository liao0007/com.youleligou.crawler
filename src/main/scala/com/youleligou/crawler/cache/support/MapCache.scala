package com.youleligou.crawler.cache.support

import com.youleligou.crawler.cache.Cache

import scala.collection.mutable

/**
  * Created by dell on 2016/9/2.
  * 采用本地Map实现的缓存
  */
private[crawler] class MapCache[KEY, VALUE] extends Cache[KEY, VALUE] {

  private val map = new mutable.HashMap[KEY, VALUE]()

  override def contains(key: KEY): Boolean = map.contains(key)

  override def get(key: KEY): Option[VALUE] = map.get(key)

  override def put(key: KEY, value: VALUE): Unit = map.put(key, value)

  override def size(): Int = map.size

  override def keys(): scala.collection.Set[KEY] = map.keySet
}
