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
import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by liangliao on 5/4/17.
  */
trait ProxyAssistantService {
  /*
  load from database to redis
   */
  def load: Future[Loaded]

  /*
  clean up invalid
   */
  def cleanUp: Future[Long]

  /*
  get by FIFO
   */
  def get(limit: Int): Future[Seq[CrawlerProxyServer]]
}

object ProxyAssistantService {
  final val ProxyQueueName = "ProxyQueue"
}

class DefaultProxyAssistantService @Inject()(redisClient: RedisClient,
                                             crawlerProxyServerRepo: CrawlerProxyServerRepo,
                                             standaloneAhcWSClient: StandaloneAhcWSClient)
  extends ProxyAssistantService {

  def load: Future[Loaded] = crawlerProxyServerRepo.all(100).flatMap { proxyServers =>
    redisClient.rpush[String](ProxyAssistantService.ProxyQueueName, proxyServers map (Json.toJson(_).toString()): _*).map(_.toInt).map(Loaded)
  }

  def cleanUp: Future[Long] =
    redisClient.lpop[String](ProxyAssistantService.ProxyQueueName) flatMap {
      case Some(crawlerProxyServerString) =>
        Json.parse(crawlerProxyServerString).validate[CrawlerProxyServer].asOpt.map {
          _.copy(isLive = true, lastVerifiedAt = Some(new Timestamp(System.currentTimeMillis())))
        } map { proxyServer =>
          standaloneAhcWSClient
            .url("http://www.baidu.com")
            .withProxyServer(DefaultWSProxyServer(proxyServer.ip, proxyServer.port))
            .withRequestTimeout(Duration(2, SECONDS))
            .get()
            .flatMap { response =>
              if (response.status == 200) {
                redisClient.rpush(ProxyAssistantService.ProxyQueueName, Json.toJson(proxyServer).toString)
              } else {
                crawlerProxyServerRepo.update(proxyServer.id, proxyServer.isLive, proxyServer.lastVerifiedAt.get).map(_.toLong)
              }
            }
        } getOrElse Future.successful(0L)
      case _ =>
        Future.successful(0L)
    }

  def get(limit: Int): Future[Seq[CrawlerProxyServer]] = {
    val futureStrings = (1 to limit).map { _ =>
      redisClient.rpoplpush[String](ProxyAssistantService.ProxyQueueName, ProxyAssistantService.ProxyQueueName)
    }

    Future.sequence(futureStrings).map { proxyServerString =>
      proxyServerString.flatten.flatMap { proxyServerString =>
        Json.parse(proxyServerString).validate[CrawlerProxyServer].asOpt
      }
    }
  }
}
