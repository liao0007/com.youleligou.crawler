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
import scala.util.Try

/**
  * Created by liangliao on 5/4/17.
  */
trait ProxyAssistantService extends LazyLogging {
  val redisClient: RedisClient
  val standaloneAhcWSClient: StandaloneAhcWSClient
  val crawlerProxyServerRepo: CrawlerProxyServerRepo

  val cachedProxyQueueKey: String     = ProxyAssistantService.ProxyQueuePrefix
  val cachedLiveProxyQueueKey: String = ProxyAssistantService.LiveProxyQueuePrefix

  def currentTimestamp = new Timestamp(System.currentTimeMillis())

  def cacheSize()(implicit executor: ExecutionContext): Future[Cached]
  /*
  load from database to redis
   */
  def loadCache()(implicit executor: ExecutionContext): Future[CacheLoaded]

  /*
  clean up invalid
   */
  def clean()(implicit executor: ExecutionContext): Future[Any]

  /*
  get by FIFO
   */
  def get()(implicit executor: ExecutionContext): Future[CachedProxyServer]

  protected def testAvailability(proxyServer: CrawlerProxyServer)(implicit executor: ExecutionContext): Future[CrawlerProxyServer] = {
    Try {
      standaloneAhcWSClient
        .url("http://www.baidu.com")
        .withProxyServer(DefaultWSProxyServer(proxyServer.ip, proxyServer.port))
        .withRequestTimeout(Duration(2, SECONDS))
        .get()
        .map { response =>
          if (response.status == 200 && response.body.contains("百度一下，你就知道")) {
            proxyServer.copy(isLive = true, lastVerifiedAt = Some(currentTimestamp))
          } else {
            proxyServer.copy(isLive = false, lastVerifiedAt = Some(currentTimestamp))
          }
        } recover {
        case _: Throwable =>
          proxyServer.copy(isLive = false, lastVerifiedAt = Some(currentTimestamp))
      }
    } getOrElse {
      Future.successful(proxyServer.copy(isLive = false, lastVerifiedAt = Some(currentTimestamp)))
    }
  } recover {
    case x: Throwable =>
      logger.warn(x.getMessage)
      proxyServer.copy(isLive = false, lastVerifiedAt = Some(currentTimestamp))
  }

}

object ProxyAssistantService {
  final val ProxyQueuePrefix     = "ProxyQueue"
  final val LiveProxyQueuePrefix = "LiveProxyQueue"
}

class DefaultProxyAssistantService @Inject()(val redisClient: RedisClient,
                                             val crawlerProxyServerRepo: CrawlerProxyServerRepo,
                                             val standaloneAhcWSClient: StandaloneAhcWSClient)
    extends ProxyAssistantService {

  def cacheSize()(implicit executor: ExecutionContext): Future[Cached] =
    redisClient.llen(cachedLiveProxyQueueKey).map(_.toInt).map(Cached) recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        Cached(0)
    }

  def loadCache()(implicit executor: ExecutionContext): Future[CacheLoaded] =
    redisClient.del(cachedProxyQueueKey) flatMap { _ =>
      crawlerProxyServerRepo.all().flatMap { proxyServers =>
        redisClient
          .lpush[String](cachedProxyQueueKey, proxyServers map (Json.toJson(_).toString()): _*)
          .map(_.toInt)
          .map(CacheLoaded)
      }
    } recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        CacheLoaded(0)
    }

  def clean()(implicit executor: ExecutionContext): Future[Any] =
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
    } recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
    }

  def get()(implicit executor: ExecutionContext): Future[CachedProxyServer] =
    redisClient.rpop[String](cachedLiveProxyQueueKey) map {
      case Some(proxyServerString) =>
        Json.parse(proxyServerString).validate[CrawlerProxyServer].asOpt match {
          case Some(proxyServer) =>
            CachedProxyServer(Some(proxyServer))

            //without test
//            testAvailability(proxyServer) map { testedLiveProxyServer =>
//              crawlerProxyServerRepo.insertOrUpdate(testedLiveProxyServer)
//              val testedProxyServerString = Json.toJson(testedLiveProxyServer).toString()
//              if (testedLiveProxyServer.isLive) {
//                redisClient.lpush(cachedLiveProxyQueueKey, testedProxyServerString)
//                CachedProxyServer(Some(testedLiveProxyServer))
//              } else {
//                CachedProxyServer(None)
//              }
//            }
          case _ =>
            CachedProxyServer(None)
        }
      case _ => CachedProxyServer(None)
    } recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        CachedProxyServer(None)
    }

}
