package com.youleligou.crawler.actor

import com.google.inject.Inject

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actor.CountActor._
import com.youleligou.crawler.model._

/**
  * 抓取种子注入任务,将需要抓取的任务注入到该任务中
  */
class InjectActor @Inject()(config: Config, @Named(FetchActor.poolName) fetchActor: ActorRef, @Named(CountActor.poolName) countActor: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case urlInfo: UrlInfo if urlInfo.url.startsWith("http") =>
      log.info("inject url: " + urlInfo)
      fetchActor ! urlInfo
      countActor ! InjectCounter(1)
  }
}

object InjectActor extends NamedActor {
  override final val name = "InjectActor"
  override final val poolName = "InjectActorPool"
}
