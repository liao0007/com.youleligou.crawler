package com.youleligou.proxyHunters.xicidaili

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.Injector
import com.youleligou.crawler.models.{FetchRequest, UrlInfo}

import scala.concurrent.duration._

class XiCiDaiLiCrawlerBootstrap @Inject()(config: Config, system: ActorSystem, @Named(Injector.PoolName) injectors: ActorRef) extends LazyLogging {
  import system.dispatcher

  val proxyListConfig = config.getConfig("crawler.job.xicidaili.proxyList")

  def start(): Unit = {
    import com.github.andr83.scalaconfig._

    val proxyListJobType = proxyListConfig.getString("jobType")

    implicit val timeout = Timeout(5.minutes)
    injectors ? Injector.ClearCache(proxyListJobType) map {
      case Injector.CacheCleared(_) =>
        val seeds = proxyListConfig.as[Seq[UrlInfo]]("seed")
        seeds.foreach { seed =>
          injectors ! Injector.Inject(FetchRequest(
                                        urlInfo = seed
                                      ),
                                      force = true)
        }

        system.scheduler.schedule(1.seconds,
                                  FiniteDuration(proxyListConfig.getInt("interval"), MILLISECONDS),
                                  injectors,
                                  Injector.Tick(proxyListJobType))

      case _ => logger.warn("food injector cache clear failed")
    }

  }

}
