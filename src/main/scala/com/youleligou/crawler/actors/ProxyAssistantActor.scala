package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, Stash}
import akka.pattern.pipe
import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.actors.ProxyAssistantActor._
import com.youleligou.crawler.daos.CrawlerProxyServer
import com.youleligou.crawler.services.ProxyAssistantService

class ProxyAssistantActor @Inject()(config: Config, proxyAssistantService: ProxyAssistantService) extends Actor with Stash with ActorLogging {
  import context.dispatcher

  override def receive: Receive = {
    case Init =>
      proxyAssistantService.init()

    case Clean =>
      proxyAssistantService.clean()

    case GetProxyServer =>
      proxyAssistantService.get.map {
        case Some(proxyServer) => ProxyServerAvailable(proxyServer)
        case _                 => ProxyServerUnavailable
      } pipeTo sender()
  }
}

object ProxyAssistantActor extends NamedActor {
  override final val name     = "ProxyAssistantActor"
  override final val poolName = "ProxyAssistantActorPool"

  sealed trait Command
  sealed trait Event

  case object Init  extends Command
  case object Clean extends Command

  case object GetProxyServer                                  extends Command
  case class ProxyServerAvailable(server: CrawlerProxyServer) extends Event
  case object ProxyServerUnavailable                          extends Event
}
