package com.youleligou.proxyHunters.xicidaili

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractInjectActor
import com.youleligou.crawler.actors.AbstractInjectActor.{CacheCleared, ClearCache, Tick}
import com.youleligou.crawler.models.{FetchRequest, UrlInfo}

import scala.concurrent.duration._

class XiCiDaiLiCrawlerBootstrap @Inject()(config: Config, system: ActorSystem, @Named(ProxyListInjectActor.poolName) injectorPool: ActorRef)
    extends LazyLogging {
  import system.dispatcher

  val xiciConfig = config.getConfig("crawler.job.xicidaili")

  def start(): Unit = {
    import com.github.andr83.scalaconfig._

    val config = xiciConfig.getConfig("proxy-list")

    implicit val timeout = Timeout(5.minutes)
    injectorPool ? ClearCache map {
      case CacheCleared(_) =>
        val seeds = config.as[Seq[UrlInfo]]("seed")
        seeds.foreach { seed =>
          injectorPool ! AbstractInjectActor.Inject(FetchRequest(
                                                      requestName = "fetch_xici_proxy_list",
                                                      urlInfo = seed
                                                    ),
                                                    force = true)
        }

        system.scheduler.schedule(1.seconds, FiniteDuration(config.getInt("interval"), MILLISECONDS), injectorPool, Tick)

      case _ => logger.warn("food injector cache clear failed")
    }

  }

}
