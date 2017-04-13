package com.youleligou.crawler.actors

import java.sql.Timestamp

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.actors.ProxyAssistantActor._
import com.youleligou.crawler.daos.{CrawlerProxyServer, CrawlerProxyServerRepo}
import play.api.libs.json.Json
import play.api.libs.ws.DefaultWSProxyServer
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import redis.RedisClient

import scala.concurrent.duration.{Duration, MILLISECONDS}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.control.NonFatal

class ProxyAssistantActor @Inject()(config: Config,
                                    redisClient: RedisClient,
                                    standaloneAhcWSClient: StandaloneAhcWSClient,
                                    crawlerProxyServerRepo: CrawlerProxyServerRepo)
    extends Actor
    with ActorLogging {
  import context.dispatcher

  val ProxyQueueKey: String   = "ProxyQueue"
  val LiveProxySetKey: String = "LiveProxySet"
  val timeout                 = Duration(config.getInt("crawler.proxy-assistant.timeout"), MILLISECONDS)

  private def currentTimestamp = new Timestamp(System.currentTimeMillis())

  override def receive: Receive = {
    case Init =>
      log.info("{} Init", self.path)
      Try {
        redisClient.del(ProxyQueueKey) flatMap { _ =>
          crawlerProxyServerRepo.all().flatMap { proxyServers =>
            redisClient
              .lpush[String](ProxyQueueKey, proxyServers map (Json.toJson(_).toString()): _*)
          }
        } recover {
          case NonFatal(x) =>
            log.warning("{} {}", self.path, x.getMessage)
            0L
        }
      } getOrElse 0L

    case Replenish =>
      log.info("{} replenish", self.path)
      Try {
        redisClient.rpop[String](ProxyQueueKey) flatMap {
          case Some(proxyServerString) =>
            Json.parse(proxyServerString).validate[CrawlerProxyServer].asOpt match {
              case Some(proxyServer) =>
                testAvailability(proxyServer) flatMap { testedProxyServer =>
                  log.info("{} {}:{} isLive={}", self.path, testedProxyServer.ip, testedProxyServer.port, testedProxyServer.isLive)
                  crawlerProxyServerRepo.insertOrUpdate(testedProxyServer)
                  redisClient.lpush(ProxyQueueKey, Json.toJson(testedProxyServer).toString()) flatMap { _ =>
                    if (testedProxyServer.isLive) {
                      redisClient.sadd(LiveProxySetKey, Json.toJson(testedProxyServer.copy(lastVerifiedAt = None)).toString())
                    } else {
                      Future.successful(0L)
                    }
                  }
                }
              case _ => Future.successful(0L)
            }
          case _ => Future.successful(0L)
        } recover {
          case NonFatal(x) =>
            log.warning("{} {}", self.path, x.getMessage)
            0L
        }
      } getOrElse Future.successful(0L)
  }

  protected def testAvailability(proxyServer: CrawlerProxyServer)(implicit executor: ExecutionContext): Future[CrawlerProxyServer] = {
    Try {
      standaloneAhcWSClient
        .url("http://www.baidu.com")
        .withProxyServer(DefaultWSProxyServer(proxyServer.ip, proxyServer.port))
        .withRequestTimeout(timeout)
        .get()
        .map { response =>
          if (response.status == 200 && response.body.contains("百度一下，你就知道")) {
            proxyServer.copy(isLive = true, lastVerifiedAt = Some(currentTimestamp), checkCount = 0)
          } else {
            proxyServer.copy(isLive = false, lastVerifiedAt = Some(currentTimestamp), checkCount = proxyServer.checkCount + 1)
          }
        } recover {
        case NonFatal(_) =>
          proxyServer.copy(isLive = false, lastVerifiedAt = Some(currentTimestamp), checkCount = proxyServer.checkCount + 1)
      }
    } getOrElse {
      Future.successful(proxyServer.copy(isLive = false, lastVerifiedAt = Some(currentTimestamp), checkCount = proxyServer.checkCount + 1))
    }
  } recover {
    case NonFatal(x) =>
      log.warning("{} {}", self.path, x.getMessage)
      proxyServer.copy(isLive = false, lastVerifiedAt = Some(currentTimestamp), checkCount = proxyServer.checkCount + 1)
  }
}

object ProxyAssistantActor extends NamedActor {
  override final val name     = "ProxyAssistantActor"
  override final val poolName = "ProxyAssistantActorPool"

  sealed trait Command
  sealed trait Event

  case object Init      extends Command
  case object Replenish extends Command

  case object GetProxyServer                                  extends Command
  case class ProxyServerAvailable(server: CrawlerProxyServer) extends Event
  case object ProxyServerUnavailable                          extends Event
}

object ProxyReplenishmentAssistantActor extends NamedActor {
  final val name     = "ProxyReplenishmentAssistantActor"
  final val poolName = "ProxyReplenishmentAssistantActorPool"
}
