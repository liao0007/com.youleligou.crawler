package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, Stash}
import akka.pattern.pipe
import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.actors.ProxyAssistantActor.{CleanUp, Get, Loaded}
import com.youleligou.crawler.services.ProxyAssistantService

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._

class ProxyAssistantActor @Inject()(config: Config, proxyAssistantService: ProxyAssistantService) extends Actor with Stash with ActorLogging {

  override def receive: Receive = proxyCacheUnavailable

  def proxyCacheUnavailable: Receive = {
    case _ =>
      log.info("proxy cache unavailable, loading")
      stash()
      pipe(proxyAssistantService.load) to self
      context.become(proxyCacheLoading)
  }

  def proxyCacheLoading: Receive = {
    case Loaded(proxyServerCount) =>
      log.info(s"loaded $proxyServerCount proxy server(s) to cache")
      context.system.scheduler.schedule(FiniteDuration(2, SECONDS), FiniteDuration(5, MINUTES), self, CleanUp)
      unstashAll()
      context.become(proxyCacheAvailable)
    case _ =>
      log.info("still loading proxy servers")
      stash()
  }

  def proxyCacheAvailable: Receive = {
    case CleanUp =>
      log.info("scheduled cleaning up proxy server caches")
      proxyAssistantService.cleanUp

    case Get(limit) =>
      log.info(s"request and return $limit proxy server list")
      pipe(proxyAssistantService.get(limit)) to sender()
  }
}

object ProxyAssistantActor extends NamedActor {
  override final val name = "ProxyAssistantActor"
  override final val poolName = "ProxyAssistantActorPool"

  case class Loaded(proxyServerCount: Int)

  case object CleanUp

  case class Get(limit: Int)
}
