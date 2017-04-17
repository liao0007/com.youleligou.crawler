package com.youleligou.proxyHunters.youdaili

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractInjectActor
import com.youleligou.crawler.actors.AbstractInjectActor.{ClearCache, Tick}
import com.youleligou.crawler.models.{FetchRequest, UrlInfo}

import scala.concurrent.duration._

class YouDaiLiCrawlerBootstrap @Inject()(config: Config,
                                         system: ActorSystem,
                                         @Named(ProxyPageInjectActor.poolName) pageInjectorPool: ActorRef,
                                         @Named(ProxyListInjectActor.poolName) listInjectorPool: ActorRef)
    extends LazyLogging {
  import system.dispatcher

  val youdailiConfig = config.getConfig("crawler.job.youdaili")

  def start(): Unit = {
    import com.github.andr83.scalaconfig._

    val config           = youdailiConfig.getConfig("proxy-page")
    implicit val timeout = Timeout(5.minutes)

    for {
      _ <- pageInjectorPool ? ClearCache
      _ <- listInjectorPool ? ClearCache
    } yield {
      val seeds = config.as[Seq[UrlInfo]]("seed")
      seeds.foreach { seed =>
        pageInjectorPool ! AbstractInjectActor.Inject(FetchRequest(
                                                        requestName = "fetch_youdaili_proxy_page",
                                                        urlInfo = seed
                                                      ),
                                                      force = true)
      }

      system.scheduler.schedule(0.seconds, FiniteDuration(config.getInt("interval"), MILLISECONDS), pageInjectorPool, Tick)
      system.scheduler.schedule(30.seconds, FiniteDuration(youdailiConfig.getInt("proxy-list.interval"), MILLISECONDS), listInjectorPool, Tick)
    }
  }

}
