package com.youleligou.proxyHunters.youdaili

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractInjectActor.GenerateFetch
import com.youleligou.proxyHunters.youdaili.actors.ProxyListInjectActor

import scala.concurrent.duration._

class YouDaiLiCrawlerBootstrap @Inject()(config: Config, system: ActorSystem, @Named(ProxyListInjectActor.poolName) injectActor: ActorRef)
  extends LazyLogging {

  import system.dispatcher

  def start(interval: FiniteDuration): Unit = {
    system.scheduler.schedule(FiniteDuration(2, SECONDS), interval, injectActor, GenerateFetch)
  }

}
