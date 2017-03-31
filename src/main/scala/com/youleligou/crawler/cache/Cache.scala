package com.youleligou.crawler.cache

/**
  * Created by dell on 2016/9/2.
  * 缓存接口
  */
trait Cache[KEY, VALUE] {

  def contains(key: KEY): Boolean

  def put(key: KEY, value: VALUE)

  def get(key: KEY): Option[VALUE]

  def size(): Int

  def keys(): scala.collection.Set[KEY]

}
