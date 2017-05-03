package com.youleligou.crawler.actors

import java.io.{File, PrintWriter}
import java.sql.Timestamp
import java.time.LocalDateTime

import akka.actor.{Actor, ActorLogging}
import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.core.reps.Repo
import com.youleligou.crawler.actors.ProxyAssistant._
import com.youleligou.crawler.daos.ProxyServerDao
import org.joda.time.DateTime
import play.api.libs.ws.DefaultWSProxyServer
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import redis.RedisClient

import scala.concurrent.duration.{Duration, MILLISECONDS}
import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process._
import scala.util.control.NonFatal

class ProxyAssistant @Inject()(config: Config,
                               redisClient: RedisClient,
                               proxyServerRepo: Repo[ProxyServerDao],
                               standaloneAhcWSClient: StandaloneAhcWSClient)
    extends Actor
    with ActorLogging {
  import context.dispatcher

  val timeout = Duration(config.getInt("crawler.proxy-assistant.timeout"), MILLISECONDS)

  private def now = Timestamp.valueOf(LocalDateTime.now())

  override def receive: Receive = {
    case Run =>
      log.debug("{} start run", self.path)
      proxyServerRepo.all() flatMap { proxyServers =>
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

        try {
          val writer = new PrintWriter(new File(config.getString("proxy.squid-config-file")))
          try {
            log.info("{} write to squid config file", self.path)
            writer.write(squidConfig)

            log.info("{} restart squid", self.path)
            config.getString("proxy.squid-reload-command") !
          } catch {
            case NonFatal(x) =>
              log.warning(x.getMessage)
          } finally {
            writer.close()
          }
        } catch {
          case NonFatal(x) =>
            log.warning(x.getMessage)
        }

        proxyServerRepo.save(testedProxyServers)
      }

  }

  protected def testAvailability(proxyServer: ProxyServerDao)(implicit executor: ExecutionContext): Future[ProxyServerDao] = {
    try {
      //ping first
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
        case NonFatal(x) =>
          log.debug("{} {}", self.path, x.getMessage)
          proxyServer.copy(isLive = false, lastVerifiedAt = Some(now), checkCount = proxyServer.checkCount + 1)
      }
    } catch {
      case NonFatal(x) =>
        log.debug("{} {}", self.path, x.getMessage)
        Future.successful(proxyServer.copy(isLive = false, lastVerifiedAt = Some(now), checkCount = proxyServer.checkCount + 1))
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
