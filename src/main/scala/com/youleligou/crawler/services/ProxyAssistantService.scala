package com.youleligou.crawler.services

import java.sql.Timestamp

import com.google.inject.Inject
import com.youleligou.crawler.actors.ProxyAssistantActor.Loaded
import com.youleligou.crawler.daos.{CrawlerProxyServer, CrawlerProxyServerRepo}
import play.api.libs.json.Json
import play.api.libs.ws.DefaultWSProxyServer
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, TimeoutException}

/**
  * Created by liangliao on 5/4/17.
  */
trait ProxyAssistantService {
  /*
  load from database to redis
   */
  def load(queueId: String): Future[Loaded]

  /*
  clean up invalid
   */
  def cleanUp(queueId: String): Future[Long]

  /*
  get by FIFO
   */
  def get(queueId: String, limit: Int): Future[Seq[CrawlerProxyServer]]
}

object ProxyAssistantService {
  final val ProxyQueuePrefix = "ProxyQueue"
}

class DefaultProxyAssistantService @Inject()(redisClient: RedisClient,
                                             crawlerProxyServerRepo: CrawlerProxyServerRepo,
                                             standaloneAhcWSClient: StandaloneAhcWSClient)
  extends ProxyAssistantService {

  def load(queueId: String): Future[Loaded] = crawlerProxyServerRepo.all(100).flatMap { proxyServers =>
    redisClient.rpush[String](ProxyAssistantService.ProxyQueuePrefix + queueId, proxyServers map (Json.toJson(_).toString()): _*).map(_.toInt).map(Loaded)
  }

  def cleanUp(queueId: String): Future[Long] =
    try {
      redisClient.lpop[String](ProxyAssistantService.ProxyQueuePrefix + queueId) flatMap {
        case Some(crawlerProxyServerString) =>
          Json.parse(crawlerProxyServerString).validate[CrawlerProxyServer].asOpt.map {
            _.copy(isLive = true, lastVerifiedAt = Some(new Timestamp(System.currentTimeMillis())))
          } map { proxyServer =>
            standaloneAhcWSClient
              .url("http://www.baidu.com")
              .withProxyServer(DefaultWSProxyServer(proxyServer.ip, proxyServer.port))
              .withRequestTimeout(Duration(3, SECONDS))
              .get()
              .flatMap { response =>
                if (response.status == 200) {
                  redisClient.rpush(ProxyAssistantService.ProxyQueuePrefix + queueId, Json.toJson(proxyServer).toString)
                } else {
                  crawlerProxyServerRepo.update(proxyServer.id, proxyServer.isLive, proxyServer.lastVerifiedAt.get).map(_.toLong)
                }
              } recover {
              case _: TimeoutException => 0L
              case _ => 0L
            }

          } getOrElse Future.successful(0L)

        case _ =>
          Future.successful(0L)
      }
    } catch {
      case _: Throwable => Future.successful(0L)
    }

  def get(queueId: String, limit: Int): Future[Seq[CrawlerProxyServer]] = {
    val futureStrings = (1 to limit).map { _ =>
      redisClient.rpoplpush[String](ProxyAssistantService.ProxyQueuePrefix + queueId, ProxyAssistantService.ProxyQueuePrefix + queueId)
    }

    Future.sequence(futureStrings).map { proxyServerString =>
      proxyServerString.flatten.flatMap { proxyServerString =>
        Json.parse(proxyServerString).validate[CrawlerProxyServer].asOpt
      }
    }
  }
}
