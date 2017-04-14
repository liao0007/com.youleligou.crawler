package com.youleligou.crawler.actors

import java.io.{File, PrintWriter}
import java.sql.Timestamp

import akka.actor.{Actor, ActorLogging}
import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.actors.ProxyAssistantActor._
import com.youleligou.crawler.daos.{CrawlerProxyServer, CrawlerProxyServerRepo}
import play.api.libs.ws.DefaultWSProxyServer
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import redis.RedisClient

import scala.concurrent.duration.{Duration, MILLISECONDS}
import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process._
import scala.util.control.NonFatal

class ProxyAssistantActor @Inject()(config: Config,
                                    redisClient: RedisClient,
                                    standaloneAhcWSClient: StandaloneAhcWSClient,
                                    crawlerProxyServerRepo: CrawlerProxyServerRepo)
    extends Actor
    with ActorLogging {
  import context.dispatcher

  val timeout = Duration(config.getInt("crawler.proxy-assistant.timeout"), MILLISECONDS)

  private def currentTimestamp = new Timestamp(System.currentTimeMillis())

  override def receive: Receive = {
    case Run =>
      log.info("{} run", self.path)
      crawlerProxyServerRepo.all() flatMap { proxyServers =>
        Future.sequence(proxyServers map { proxyServer =>
          testAvailability(proxyServer) map { testedProxyServer =>
            log.info("{} {}:{} isLive={}", self.path, testedProxyServer.ip, testedProxyServer.port, testedProxyServer.isLive)
            testedProxyServer
          }
        })
      } map { testedProxyServers =>
        //update squid config file
        val squidConfig = testedProxyServers.filter(_.isLive) map { liveProxyServers =>
          s"""cache_peer ${liveProxyServers.ip} parent ${liveProxyServers.port} 0 round-robin no-query no-digest"""
        } mkString "\n"

        try {
          val writer = new PrintWriter(new File(config.getString("proxy.squid-config-file")))
          writer.write(squidConfig)
          writer.close()
          config.getString("proxy.squid-reload-command") !

        } catch {
          case NonFatal(x) =>
            log.warning(x.getMessage)
        }
        testedProxyServers

      } map crawlerProxyServerRepo.insertOrUpdate //update database

  }

  protected def testAvailability(proxyServer: CrawlerProxyServer)(implicit executor: ExecutionContext): Future[CrawlerProxyServer] = {
    try {
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
    } catch {
      case NonFatal(_) =>
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
  case object Run extends Command
}
