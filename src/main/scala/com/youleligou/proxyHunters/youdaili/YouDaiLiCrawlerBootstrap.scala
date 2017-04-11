package com.youleligou.proxyHunters.youdaili

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractInjectActor
import com.youleligou.crawler.actors.AbstractInjectActor.Tick
import com.youleligou.crawler.models.{FetchRequest, UrlInfo}

import scala.concurrent.duration._

class YouDaiLiCrawlerBootstrap @Inject()(config: Config, system: ActorSystem, @Named(ProxyListInjectActor.poolName) injectorPool: ActorRef)
    extends LazyLogging {
  import system.dispatcher

  def start(): Unit = {
    import com.github.andr83.scalaconfig._
//    val seeds = config.as[Seq[UrlInfo]]("crawler.seed.youdaili.proxy-page")
//    seeds.foreach { seed =>
//      injectorPool ! AbstractInjectActor.Inject(FetchRequest(
//                                                  requestName = "fetch_youdaili_proxy_page",
//                                                  urlInfo = seed
//                                                ),
//                                                force = true)
//    }

    system.scheduler.schedule(1.seconds, 500.millis, injectorPool, Tick)
  }

}
