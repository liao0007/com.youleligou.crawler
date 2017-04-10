package com.youleligou.proxyHunters.xicidaili

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractInjectActor
import com.youleligou.crawler.models.UrlInfo.UrlInfoType
import com.youleligou.crawler.models.{FetchRequest, UrlInfo}
import com.youleligou.proxyHunters.xicidaili.actors.ProxyListInjectActor

class XiCiDaiLiCrawlerBootstrap @Inject()(config: Config, @Named(ProxyListInjectActor.poolName) injectActor: ActorRef) extends LazyLogging {

  def start(): Unit = {
    injectActor ! AbstractInjectActor.Inject(
      FetchRequest(
        requestName = "fetch_xicidaili_list",
        urlInfo = UrlInfo(
          host = "http://www.xicidaili.com/nt/1",
          urlType = UrlInfoType.Seed
        )
      ))

  }

}
