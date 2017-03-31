package com.youleligou.crawler.caches

import javax.inject.Inject

import com.typesafe.config.Config
import redis.RedisClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

/**
 * 采用Redis实现的缓存
 */
class RedisCache @Inject()(config: Config, redisClient: RedisClient) extends Cache {
  val expire: Long = config.getLong("cache.redis.expire")

  override def contains(key: String): Future[Boolean] = {
    redisClient.exists(key)
  }

  override def get(key: String): Future[Option[String]] = {
    redisClient.get(key)
  }

  override def put(key: String, value: String): Future[Boolean] = {
    redisClient.setex(key, expire, value)
  }

  override def size(): Future[Long] = {
    redisClient.dbsize()
  }

  override def keys(): Set[String] = throw new Exception("unsupport operation")
}
