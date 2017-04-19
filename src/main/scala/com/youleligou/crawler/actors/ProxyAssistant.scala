package com.youleligou.crawler.actors

import java.io.{File, PrintWriter}

import akka.actor.{Actor, ActorLogging}
import com.google.inject.Inject
import com.outworkers.phantom.database.DatabaseProvider
import com.typesafe.config.Config
import com.youleligou.crawler.actors.ProxyAssistant._
import com.youleligou.crawler.daos.cassandra.{CrawlerDatabase, CrawlerProxyServer}
import org.joda.time.DateTime
import play.api.libs.ws.DefaultWSProxyServer
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import redis.RedisClient

import scala.concurrent.duration.{Duration, MILLISECONDS}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.control.NonFatal

class ProxyAssistant @Inject()(config: Config, redisClient: RedisClient, val database: CrawlerDatabase, standaloneAhcWSClient: StandaloneAhcWSClient)
    extends Actor
    with ActorLogging
    with DatabaseProvider[CrawlerDatabase] {
  import context.dispatcher

  val timeout = Duration(config.getInt("crawler.proxy-assistant.timeout"), MILLISECONDS)

  private def now = DateTime.now()

  override def receive: Receive = {
    case Run =>
      log.debug("{} run", self.path)
      database.crawlerProxyServers.all flatMap { proxyServers =>
        Future.sequence(proxyServers map { proxyServer =>
          testAvailability(proxyServer) map { testedProxyServer =>
            log.debug("{} {}:{} isLive={}", self.path, testedProxyServer.ip, testedProxyServer.port, testedProxyServer.isLive)
            testedProxyServer
          }
        })
      } map { testedProxyServers =>
        //update squid config file
        val squidConfig = testedProxyServers
          .filter(_.isLive)
          .groupBy(_.ip)
          .map(_._2.head) map { liveProxyServers => // group by and map to remove duplicate ip with diff ports
          s"""cache_peer ${liveProxyServers.ip} parent ${liveProxyServers.port} 0 round-robin no-query no-digest"""
        } mkString "\n"

        val writer = new PrintWriter(new File(config.getString("proxy.squid-config-file")))
        try {
          log.info("{} write to squid config file", self.path)
          writer.write(squidConfig)

//          log.info("{} restart squid", self.path)
//          config.getString("proxy.squid-reload-command") !
        } catch {
          case NonFatal(x) =>
            log.warning(x.getMessage)
        } finally {
          writer.close()
        }

        database.crawlerProxyServers.batchInsertOrUpdate(testedProxyServers)
      }

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
            proxyServer.copy(isLive = true, lastVerifiedAt = Some(now), checkCount = 0)
          } else {
            proxyServer.copy(isLive = false, lastVerifiedAt = Some(now), checkCount = proxyServer.checkCount + 1)
          }
        } recover {
        case NonFatal(_) =>
          proxyServer.copy(isLive = false, lastVerifiedAt = Some(now), checkCount = proxyServer.checkCount + 1)
      }

    } getOrElse {
      Future.successful(proxyServer.copy(isLive = false, lastVerifiedAt = Some(now), checkCount = proxyServer.checkCount + 1))

    } recover {
      case NonFatal(x) =>
        log.warning("{} {}", self.path, x.getMessage)
        proxyServer.copy(isLive = false, lastVerifiedAt = Some(now), checkCount = proxyServer.checkCount + 1)

    }
  }
}

object ProxyAssistant extends NamedActor {
  override final val Name     = "ProxyAssistantActor"
  override final val PoolName = "ProxyAssistantActorPool"

  sealed trait Command
  sealed trait Event
  case object Run extends Command
}
