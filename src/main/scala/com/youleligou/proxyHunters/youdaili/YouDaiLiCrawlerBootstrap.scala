package com.youleligou.proxyHunters.youdaili

import akka.actor._
import akka.util.Timeout
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractInjectActor
import com.youleligou.crawler.actors.AbstractInjectActor.{CacheCleared, ClearCache, Tick}
import com.youleligou.crawler.models.{FetchRequest, UrlInfo}
import akka.pattern.ask
import scala.concurrent.duration._

class YouDaiLiCrawlerBootstrap @Inject()(config: Config,
                                         system: ActorSystem,
                                         @Named(ProxyPageInjectActor.poolName) pageInjectorPool: ActorRef,
                                         @Named(ProxyListInjectActor.poolName) listInjectorPool: ActorRef)
    extends LazyLogging {
  import system.dispatcher

  def start(): Unit = {
    import com.github.andr83.scalaconfig._

    implicit val timeout = Timeout(5.minutes)

    for {
      _ <- pageInjectorPool ? ClearCache
      _ <- pageInjectorPool ? ClearCache
    } yield {
      val seeds = config.as[Seq[UrlInfo]]("crawler.seed.youdaili.proxy-page")
      seeds.foreach { seed =>
        pageInjectorPool ! AbstractInjectActor.Inject(FetchRequest(
                                                        requestName = "fetch_youdaili_proxy_page",
                                                        urlInfo = seed
                                                      ),
                                                      force = true)
      }

      system.scheduler.schedule(1.seconds, 4.seconds, pageInjectorPool, Tick)
      system.scheduler.schedule(3.seconds, 4.seconds, listInjectorPool, Tick)
    }
  }

}
