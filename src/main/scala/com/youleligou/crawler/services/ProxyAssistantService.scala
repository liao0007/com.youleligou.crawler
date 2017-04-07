package com.youleligou.crawler.services

import java.sql.Timestamp

import com.google.inject.Inject
import com.typesafe.config.Config
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
  val config: Config
  val redisClient: RedisClient
  val standaloneAhcWSClient: StandaloneAhcWSClient
  val crawlerProxyServerRepo: CrawlerProxyServerRepo

  val ProxyQueueKey: String   = ProxyAssistantService.ProxyQueueKey
  val LiveProxySetKey: String = ProxyAssistantService.LiveProxySetKey
  val timeout                 = Duration(config.getInt("crawler.actor.proxy-assistant.timeout"), MILLISECONDS)

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
        .withRequestTimeout(timeout)
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
  final val ProxyQueueKey   = "ProxyQueue"
  final val LiveProxySetKey = "LiveProxySet"
}

class DefaultProxyAssistantService @Inject()(val config: Config,
                                             val redisClient: RedisClient,
                                             val crawlerProxyServerRepo: CrawlerProxyServerRepo,
                                             val standaloneAhcWSClient: StandaloneAhcWSClient)
    extends ProxyAssistantService {

  def cacheSize()(implicit executor: ExecutionContext): Future[Cached] =
    redisClient.scard(LiveProxySetKey).map(_.toInt).map(Cached) recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        Cached(0)
    }

  def loadCache()(implicit executor: ExecutionContext): Future[CacheLoaded] =
    redisClient.del(ProxyQueueKey) flatMap { _ =>
      crawlerProxyServerRepo.all().flatMap { proxyServers =>
        redisClient
          .lpush[String](ProxyQueueKey, proxyServers map (Json.toJson(_).toString()): _*)
          .map(_.toInt)
          .map(CacheLoaded)
      }
    } recover {
      case x: Throwable =>
        logger.warn(x.getMessage)
        CacheLoaded(0)
    }

  def clean()(implicit executor: ExecutionContext): Future[Any] =
    redisClient.rpop[String](ProxyQueueKey) map {
      case Some(proxyServerString) =>
        Json.parse(proxyServerString).validate[CrawlerProxyServer].asOpt match {
          case Some(proxyServer) =>
            testAvailability(proxyServer) map { testedProxyServer =>
              logger.info(s"tested proxy server ${testedProxyServer.ip}:${testedProxyServer.port}, isLive = ${testedProxyServer.isLive}")
              crawlerProxyServerRepo.insertOrUpdate(testedProxyServer)
              redisClient.lpush(ProxyQueueKey, Json.toJson(testedProxyServer).toString())
              if (testedProxyServer.isLive) {
                redisClient.sadd(LiveProxySetKey, Json.toJson(testedProxyServer.copy(lastVerifiedAt = None)).toString())
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
    redisClient.spop[String](LiveProxySetKey) flatMap {
      case Some(proxyServerString) =>
        Json.parse(proxyServerString).validate[CrawlerProxyServer].asOpt match {
          case Some(proxyServer) =>
            testAvailability(proxyServer) map { testedLiveProxyServer =>
              crawlerProxyServerRepo.insertOrUpdate(testedLiveProxyServer)
              if (testedLiveProxyServer.isLive) {
                redisClient.sadd(LiveProxySetKey, Json.toJson(testedLiveProxyServer).toString())
                CachedProxyServer(Some(testedLiveProxyServer))
              } else {
                CachedProxyServer(None)
              }
            }
          case _ =>
            Future.successful(CachedProxyServer(None))
        }
      case _ => Future.successful(CachedProxyServer(None))
    } recover {
      case x: Throwable =>
        logger.error(x.getMessage)
        CachedProxyServer(None)
    }

}
