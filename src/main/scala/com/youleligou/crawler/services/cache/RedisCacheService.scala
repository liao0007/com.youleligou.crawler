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

  override def hexists(key: String, field: String): Future[Boolean] = {
    redisClient.hexists(key, field)
  } recover {
    case x: Throwable =>
      logger.warn(x.getMessage)
      false
  }

  override def hsetnx(key: String, field: String, value: String): Future[Boolean] =
    redisClient.hsetnx(key, field, value) recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        false
    }

  override def hlength(key: String): Future[Long] = {
    redisClient.hlen(key)
  } recover {
    case x: Throwable =>
      logger.warn(x.getMessage)
      0L
  }
}
