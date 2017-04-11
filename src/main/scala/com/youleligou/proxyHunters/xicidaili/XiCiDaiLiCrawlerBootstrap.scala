package com.youleligou.proxyHunters.xicidaili

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractInjectActor
import com.youleligou.crawler.actors.AbstractInjectActor.Tick
import com.youleligou.crawler.models.{FetchRequest, UrlInfo}

import scala.concurrent.duration._

class XiCiDaiLiCrawlerBootstrap @Inject()(config: Config, system: ActorSystem, @Named(ProxyListInjectActor.poolName) injectorPool: ActorRef)
    extends LazyLogging {
  import system.dispatcher

  def start(): Unit = {
    import com.github.andr83.scalaconfig._
    val seeds = config.as[Seq[UrlInfo]]("crawler.seed.xicidaili.proxy-list")
    seeds.foreach { seed =>
      injectorPool ! AbstractInjectActor.Inject(FetchRequest(
                                                  requestName = "fetch_xici_proxy_list",
                                                  urlInfo = seed
                                                ),
                                                force = true)
    }

  }

}
