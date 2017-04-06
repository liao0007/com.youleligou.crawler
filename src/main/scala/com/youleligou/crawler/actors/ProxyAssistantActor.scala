package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, Stash}
import akka.pattern.pipe
import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.actors.ProxyAssistantActor._
import com.youleligou.crawler.services.ProxyAssistantService

class ProxyAssistantActor @Inject()(config: Config, proxyAssistantService: ProxyAssistantService) extends Actor with Stash with ActorLogging {

  import context.dispatcher

  override def receive: Receive = proxyCacheUnavailable

  def proxyCacheUnavailable: Receive = {
    case CheckCache =>
      log.info(s"checking cache")
      proxyAssistantService.cacheSize() pipeTo self

    case Cached(proxyServerCount) if proxyServerCount > 0 =>
      log.info(s"already cached $proxyServerCount proxy server(s) to cache")
      unstashAll()
      context.become(proxyCacheAvailable)

    case Cached(proxyServerCount) if proxyServerCount <= 0 =>
      log.info(s"no cache available, now load")
      self ! LoadCache

    case LoadCache =>
      proxyAssistantService.load() pipeTo self
      unstashAll()
      context.become(proxyCacheLoading)

    case _ =>
      log.info("proxy cache unavailable, stashing")
      stash()
  }

  def proxyCacheLoading: Receive = {
    case CacheLoaded(proxyServerCount) =>
      log.info(s"loaded $proxyServerCount proxy server(s) to cache")
      unstashAll()
      context.become(proxyCacheAvailable)

    case _ =>
      log.info("still loading proxy servers")
      stash()
  }

  def proxyCacheAvailable: Receive = {
    case CleanUp =>
      log.info("scheduled cleaning up proxy server caches")
      proxyAssistantService.cleanUp()

    case Get(limit) =>
      log.info(s"requested and returning $limit proxy server list")
      proxyAssistantService.get(limit) pipeTo sender()
  }

}

object ProxyAssistantActor extends NamedActor {
  override final val name = "ProxyAssistantActor"
  override final val poolName = "ProxyAssistantActorPool"

  sealed trait ProxyAssistantActorMessage

  case object CheckCache extends ProxyAssistantActorMessage

  case class Cached(proxyServerCount: Int) extends ProxyAssistantActorMessage

  case object LoadCache extends ProxyAssistantActorMessage

  case class CacheLoaded(proxyServerCount: Int) extends ProxyAssistantActorMessage

  case object CleanUp extends ProxyAssistantActorMessage

  case class Get(limit: Int) extends ProxyAssistantActorMessage
}
