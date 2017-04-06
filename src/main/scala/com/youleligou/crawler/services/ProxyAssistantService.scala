package com.youleligou.crawler.services

import java.sql.Timestamp

import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.ProxyAssistantActor.{CacheLoaded, Cached, CachedProxyServer}
import com.youleligou.crawler.daos.{CrawlerProxyServer, CrawlerProxyServerRepo}
import play.api.libs.json.Json
import play.api.libs.ws.DefaultWSProxyServer
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import redis.RedisClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by liangliao on 5/4/17.
  */
trait ProxyAssistantService {
  val redisClient: RedisClient
  val standaloneAhcWSClient: StandaloneAhcWSClient
  val crawlerProxyServerRepo: CrawlerProxyServerRepo

  val cachedProxyQueueKey: String = ProxyAssistantService.ProxyQueuePrefix
  val cachedLiveProxyQueueKey: String = ProxyAssistantService.LiveProxyQueuePrefix

  def cacheSize()(implicit executor: ExecutionContext): Future[Cached]
  /*
  load from database to redis
   */
  def loadCache()(implicit executor: ExecutionContext): Future[CacheLoaded]

  /*
  clean up invalid
   */
  def clean()(implicit executor: ExecutionContext): Future[Unit]

  /*
  get by FIFO
   */
  def get()(implicit executor: ExecutionContext): Future[CachedProxyServer]

  protected def testAvailability(proxyServer: CrawlerProxyServer)(implicit executor: ExecutionContext): Future[CrawlerProxyServer] = {
    val currentTimestamp = new Timestamp(System.currentTimeMillis())
    standaloneAhcWSClient
      .url("http://www.baidu.com")
      .withProxyServer(DefaultWSProxyServer(proxyServer.ip, proxyServer.port))
      .withRequestTimeout(Duration(2, SECONDS))
      .get()
      .map { response =>
        if (response.status == 200) {
          proxyServer.copy(isLive = true, lastVerifiedAt = Some(currentTimestamp))
        } else {
          proxyServer.copy(isLive = false, lastVerifiedAt = Some(currentTimestamp))
        }
      } recover {
      case _: Throwable =>
        proxyServer.copy(isLive = false, lastVerifiedAt = Some(currentTimestamp))
    }
  }

}

object ProxyAssistantService {
  final val ProxyQueuePrefix = "ProxyQueue"
  final val LiveProxyQueuePrefix = "LiveProxyQueue"
}

class DefaultProxyAssistantService @Inject()(val redisClient: RedisClient,
                                             val crawlerProxyServerRepo: CrawlerProxyServerRepo,
                                             val standaloneAhcWSClient: StandaloneAhcWSClient)
  extends ProxyAssistantService
    with LazyLogging {

  def cacheSize()(implicit executor: ExecutionContext): Future[Cached] = redisClient.llen(cachedLiveProxyQueueKey).map(_.toInt).map(Cached)

  def loadCache()(implicit executor: ExecutionContext): Future[CacheLoaded] = redisClient.del(cachedProxyQueueKey) flatMap { _ =>
    crawlerProxyServerRepo.all().flatMap { proxyServers =>
      redisClient
        .lpush[String](cachedProxyQueueKey, proxyServers map (Json.toJson(_).toString()): _*)
        .map(_.toInt)
        .map(CacheLoaded)
    }
  }

  def clean()(implicit executor: ExecutionContext): Future[Unit] =
    redisClient.rpop[String](cachedProxyQueueKey) map {
      case Some(proxyServerString) =>
        Json.parse(proxyServerString).validate[CrawlerProxyServer].asOpt match {
          case Some(proxyServer) =>
            testAvailability(proxyServer) map { testedProxyServer =>
              crawlerProxyServerRepo.insertOrUpdate(testedProxyServer)
              val testedProxyServerString = Json.toJson(testedProxyServer).toString()
              redisClient.lpush(cachedProxyQueueKey, testedProxyServerString)
              if (testedProxyServer.isLive) {
                redisClient.lpush(cachedLiveProxyQueueKey, testedProxyServerString)
              }
            }
          case _ => //failed to validate
        }
      case _ => //failed to pop
    }

  def get()(implicit executor: ExecutionContext): Future[CachedProxyServer] =
    redisClient.rpop[String](cachedLiveProxyQueueKey) flatMap {
      case Some(proxyServerString) =>
        Json.parse(proxyServerString).validate[CrawlerProxyServer].asOpt match {
          case Some(proxyServer) =>
            testAvailability(proxyServer) map { testedLiveProxyServer =>
              crawlerProxyServerRepo.insertOrUpdate(testedLiveProxyServer)
              val testedProxyServerString = Json.toJson(testedLiveProxyServer).toString()
              if (testedLiveProxyServer.isLive) {
                redisClient.lpush(cachedLiveProxyQueueKey, testedProxyServerString)
                CachedProxyServer(Some(testedLiveProxyServer))
              } else {
                CachedProxyServer(None)
              }
            }
          case _ =>
            Future.successful(CachedProxyServer(None))
        }
      case _ => Future.successful(CachedProxyServer(None))
    }

}
