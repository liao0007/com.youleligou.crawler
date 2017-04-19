package com.youleligou

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.ProxyAssistant
import com.youleligou.crawler.actors.ProxyAssistant.Run

import scala.concurrent.duration._

/**
  * Created by liangliao on 18/4/17.
  */
class ProxyAssistantBootstrap @Inject()(config: Config, system: ActorSystem, @Named(ProxyAssistant.Name) proxyAssistantActor: ActorRef)
  extends LazyLogging {
  import system.dispatcher
  def start(): Unit = {
    system.scheduler.schedule(0.second, FiniteDuration(config.getInt("crawler.proxy-assistant.interval"), MILLISECONDS), proxyAssistantActor, Run)
  }
}

