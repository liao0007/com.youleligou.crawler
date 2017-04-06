package com.youleligou.crawler.services

import java.sql.Timestamp

import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.ProxyAssistantActor.{CacheLoaded, Cached}
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

  def calculateQueueId(): String = ProxyAssistantService.ProxyQueuePrefix

  def cacheSize()(implicit executor: ExecutionContext): Future[Cached]
  /*
  load from database to redis
   */
  def load()(implicit executor: ExecutionContext): Future[CacheLoaded]

  /*
  clean up invalid
   */
  def cleanUp()(implicit executor: ExecutionContext): Future[Int]

  /*
  get by FIFO
   */
  def get(limit: Int)(implicit executor: ExecutionContext): Future[Seq[CrawlerProxyServer]]

}

object ProxyAssistantService {
  final val ProxyQueuePrefix = "ProxyQueue"
}

class DefaultProxyAssistantService @Inject()(redisClient: RedisClient,
                                             crawlerProxyServerRepo: CrawlerProxyServerRepo,
                                             standaloneAhcWSClient: StandaloneAhcWSClient)
  extends ProxyAssistantService
    with LazyLogging {

  def cacheSize()(implicit executor: ExecutionContext): Future[Cached] = redisClient.llen(calculateQueueId()).map(_.toInt).map(Cached)

  def load()(implicit executor: ExecutionContext): Future[CacheLoaded] =
    crawlerProxyServerRepo.all().flatMap { proxyServers =>
      for {
        _ <- redisClient.del(calculateQueueId())
        result <- redisClient
          .rpush[String](calculateQueueId(), proxyServers map (Json.toJson(_).toString()): _*)
          .map(_.toInt)
          .map(CacheLoaded)
      } yield {
        result
      }
    }

  def cleanUp()(implicit executor: ExecutionContext): Future[Int] =
    redisClient.lpop[String](calculateQueueId()) flatMap {
      case Some(crawlerProxyServerString) =>
        Json.parse(crawlerProxyServerString).validate[CrawlerProxyServer].asOpt match {
          case Some(crawlerProxyServer) =>
            val proxyServer = crawlerProxyServer.copy(lastVerifiedAt = Some(new Timestamp(System.currentTimeMillis())))
            standaloneAhcWSClient
              .url("http://www.baidu.com")
              .withProxyServer(DefaultWSProxyServer(proxyServer.ip, proxyServer.port))
              .withRequestTimeout(Duration(3, SECONDS))
              .get()
              .flatMap { response =>
                if (response.status == 200) {
                  redisClient.rpush(calculateQueueId(), Json.toJson(proxyServer).toString).map(_.toInt)
                  crawlerProxyServerRepo.update(proxyServer.id, isLive = true, proxyServer.lastVerifiedAt.get)
                } else {
                  crawlerProxyServerRepo.update(proxyServer.id, isLive = false, proxyServer.lastVerifiedAt.get)
                }
              } recover {
              case _: Throwable =>
                crawlerProxyServerRepo.update(proxyServer.id, isLive = false, proxyServer.lastVerifiedAt.get)
                0
            }
          case None =>
            logger.error("failed to de-json" + crawlerProxyServerString)
            Future.successful(0)
        }

      case None =>
        logger.error("failed to lpop")
        Future.successful(0)
    }

  def get(limit: Int)(implicit executor: ExecutionContext): Future[Seq[CrawlerProxyServer]] =
    Future.sequence(Seq.fill(limit) {
      redisClient.rpoplpush[String](calculateQueueId(), calculateQueueId())
    }).map { proxyServerString =>
      proxyServerString.flatten.flatMap { proxyServerString =>
        Json.parse(proxyServerString).validate[CrawlerProxyServer].asOpt
      }
    }

}
