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
      proxyAssistantService.init().map { result =>
        if (result > 0) InitSuccess else InitFailed
      } pipeTo sender()

    case Clean =>
      proxyAssistantService.clean() map (_ => Cleaned) pipeTo sender()

    case CheckAvailable =>
      proxyAssistantService.checkAvailable() map { result =>
        if (result > 0) ProxyServerAvailable else ProxyServerUnavailable
      } pipeTo sender()

    case GetProxyServer =>
      proxyAssistantService.get.map {
        case Some(proxyServer) => ProxyServer(proxyServer)
        case _                 => ProxyServerUnavailable
      } pipeTo sender()
  }
}

object ProxyAssistantActor extends NamedActor {
  override final val name     = "ProxyAssistantActor"
  override final val poolName = "ProxyAssistantActorPool"

  sealed trait Event
  sealed trait Data

  case object Init        extends Event
  case object InitSuccess extends Event
  case object InitFailed  extends Event

  case object Clean   extends Event
  case object Cleaned extends Event

  case object CheckAvailable     extends Event
  case object ProxyServerAvailable    extends Event
  case object ProxyServerUnavailable extends Event

  case object GetProxyServer                         extends Event
  case class ProxyServer(server: CrawlerProxyServer) extends Data
}
