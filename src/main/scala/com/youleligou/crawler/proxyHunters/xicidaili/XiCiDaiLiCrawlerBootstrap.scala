package com.youleligou.crawler.proxyHunters.xicidaili

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractInjectActor.Init
import com.youleligou.crawler.proxyHunters.xicidaili.actors.ProxyListInjectActor

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._

class XiCiDaiLiCrawlerBootstrap @Inject()(config: Config, system: ActorSystem, @Named(ProxyListInjectActor.poolName) injectActor: ActorRef)
  extends LazyLogging {

  def start(): Unit = {
    system.scheduler.scheduleOnce(FiniteDuration(300, MILLISECONDS), injectActor, Init)
  }

}
