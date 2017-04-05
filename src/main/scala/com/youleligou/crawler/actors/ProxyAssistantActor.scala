package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.ProxyAssistantActor.{Clean, Hunt}
import com.youleligou.crawler.proxyHunters.xicidaili.XiCiDaiLiCrawlerBootstrap
import com.youleligou.crawler.services.FetchService

class ProxyAssistantActor @Inject()(config: Config,
                                    fetchService: FetchService,
                                    xiCiDaiLiCrawlerBootstrap: XiCiDaiLiCrawlerBootstrap,
                                    @Named(CountActor.poolName) countActor: ActorRef)
  extends Actor
    with ActorLogging {

  override def receive: Receive = {
    case Hunt =>
      xiCiDaiLiCrawlerBootstrap.start()

    case Clean =>
  }
}

object ProxyAssistantActor extends NamedActor {
  override final val name = "ProxyAssistantActor"
  override final val poolName = "ProxyAssistantActorPool"

  case object Hunt

  case object Clean

}
