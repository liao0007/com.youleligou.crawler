package com.youleligou.crawler.actors

import javax.inject.Inject

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.CountActor._
import com.youleligou.crawler.models._

/**
  * 抓取种子注入任务,将需要抓取的任务注入到该任务中
  */
class InjectActor @Inject()(config: Config, @Named(FetchActor.name) fetchActor: ActorRef) extends Actor with ActorLogging {
  private val countActor =
    context.system.actorSelection("akka://" + config.getString("crawler.appName") + "/user/" + CountActor.name)

  override def receive: Receive = {
    case urlInfos: List[UrlInfo] =>
      log.info("inject urls: \n" + urlInfos)
      urlInfos
        .filter(seed => seed.url.startsWith("http"))
        .foreach(seed => {
          fetchActor ! seed
          countActor ! InjectCounter(1)
        })
  }
}

object InjectActor extends NamedActor {
  override final val name = "InjectActor"
}
