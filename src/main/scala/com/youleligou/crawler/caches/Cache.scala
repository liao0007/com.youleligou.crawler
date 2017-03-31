package com.youleligou.crawler.caches

import scala.concurrent.Future

/**
  * Created by dell on 2016/9/2.
  * 缓存接口
  */
trait Cache {
  def contains(key: String): Future[Boolean]

  def put(key: String, value: String): Future[Boolean]

  def get(key: String): Future[Option[String]]

  def size(): Future[Long]

  def keys(): Future[Set[String]]
}
