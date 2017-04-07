package com.youleligou.crawler.services.cache

import com.youleligou.crawler.actors.AbstractFetchActor.FetchUrl
import com.youleligou.crawler.actors.AbstractInjectActor.HashCheckResult
import com.youleligou.crawler.services.CacheService

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

/**
  * 采用本地Map实现的缓存
  */
class MapCacheService extends CacheService {

  private val map = new mutable.HashMap[String, mutable.HashMap[String, String]]()

  def hexists(key: String, field: String): Future[Boolean] =
    Future.successful {
      map.contains(key) && map(key).contains(field)
    } recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        false
    } recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        false
    }

  def hsetnx(key: String, field: String, value: String): Future[Boolean] =
    hexists(key, field) map {
      case true =>
        false
      case _ =>
        map(key) = mutable.HashMap(field -> value)
        true
    } recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        false
    }

  def hlength(key: String): Future[Long] =
    Future.successful {
      map.get(key) map { innerMap =>
        innerMap.size.toLong
      } getOrElse 0L
    } recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        0L
    } recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        0L
    }
}
