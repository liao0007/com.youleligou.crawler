package com.youleligou.crawler.service.cache

import akka.actor.ActorSystem
import com.google.inject.Inject
import com.typesafe.config.Config
import redis.RedisClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

/**
  * 采用Redis实现的缓存
  */
class RedisCacheService @Inject()(config: Config)(implicit system: ActorSystem) extends CacheService {
  private val redisConfig: Config = config.getConfig("cache.redis")
  private val redisClient =
    RedisClient(host = redisConfig.getString("host"), port = redisConfig.getInt("port"), password = Some(redisConfig.getString("password")))

  override def contains(key: String): Future[Boolean] = {
    redisClient.exists(key)
  }

  override def get(key: String): Future[Option[String]] = {
    redisClient.get(key).map(_.map(_.toString()))
  }

  override def put(key: String, value: String): Future[Boolean] = {
    redisClient.setex(key, redisConfig.getLong("expire"), value)
  }

  override def size(): Future[Long] = {
    redisClient.dbsize()
  }
}
