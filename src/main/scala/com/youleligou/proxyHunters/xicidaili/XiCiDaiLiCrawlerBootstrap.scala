package com.youleligou.proxyHunters.xicidaili

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.proxyHunters.xicidaili.actors.ProxyListInjectActor

class XiCiDaiLiCrawlerBootstrap @Inject()(config: Config, system: ActorSystem, @Named(ProxyListInjectActor.poolName) injectActor: ActorRef)
  extends LazyLogging {

  def start(): Unit = {

  }

}
