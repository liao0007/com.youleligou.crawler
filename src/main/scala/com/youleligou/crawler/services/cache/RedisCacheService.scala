package com.youleligou.crawler.services.cache

import akka.actor.ActorSystem
import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.services.CacheService
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

/**
  * 采用Redis实现的缓存
  */
class RedisCacheService @Inject()(config: Config, redisClient: RedisClient)(implicit system: ActorSystem) extends CacheService {

  override def contains(key: String): Future[Boolean] = {
    redisClient.exists(key)
  }

  override def get(key: String): Future[Option[String]] = {
    redisClient.get(key).map(_.map(_.toString()))
  }

  override def put(key: String, value: String): Future[Boolean] = {
    redisClient.set(key, value)
  }

  override def size(): Future[Long] = {
    redisClient.dbsize()
  }
}