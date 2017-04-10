package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Stash}
import akka.pattern.pipe
import com.typesafe.config.Config
import com.youleligou.crawler.actors.AbstractFetchActor._
import com.youleligou.crawler.actors.AbstractInjectActor.Inject
import com.youleligou.crawler.actors.AbstractParseActor.Parse
import com.youleligou.crawler.actors.ProxyAssistantActor.{GetProxyServer, ProxyServerAvailable, ProxyServerUnavailable}
import com.youleligou.crawler.daos.CrawlerProxyServer
import com.youleligou.crawler.models.{FetchRequest, FetchResponse}
import com.youleligou.crawler.services.FetchService

/**
  * Created by young.yang on 2016/8/28.
  * 网页抓取任务,采用Actor实现
  */
abstract class AbstractFetchActor(config: Config,
                                  fetchService: FetchService,
                                  injectActor: ActorRef,
                                  parseActor: ActorRef,
                                  proxyAssistantActor: ActorRef)
    extends Actor
    with Stash
    with ActorLogging {

  import context.dispatcher

  final val MaxRetry = 10

  override def receive: Receive = standby

  def standby: Receive = {
    case Init =>
      proxyAssistantActor ! GetProxyServer
      context become gettingProxyServer(sender)
  }

  def gettingProxyServer(currentInjectActor: ActorRef): Receive = {
    case ProxyServerAvailable(server) =>
      currentInjectActor ! InitSucceed
      context become proxyServerAvailable(server)

    case ProxyServerUnavailable =>
      currentInjectActor ! InitFailed
      context become standby
  }

  def proxyServerAvailable(proxyServer: CrawlerProxyServer): Receive = {

    case Fetch(fetchRequest) =>
      fetchService.fetch(fetchRequest, proxyServer).map(Fetched) pipeTo self

    case Fetched(fetchResponse @ FetchResponse(FetchService.Ok, _, _, _)) =>
      log.info("{} fetch succeed", self.path.name)
      parseActor ! Parse(fetchResponse)
      unstashAll()
      context become standby

    case Fetched(FetchResponse(statusCode @ FetchService.NotFound, _, message, _)) =>
      log.info("{} fetch failed {} {}", self.path.name, statusCode, message)
      unstashAll()
      context become standby

    case Fetched(FetchResponse(statusCode @ FetchService.PaymentRequired, _, message, _)) =>
      log.info("{} fetch failed {} {}", self.path.name, statusCode, message)
      unstashAll()
      context.system.terminate()

    case Fetched(FetchResponse(statusCode @ _, _, message, fetchRequest)) if fetchRequest.retry < MaxRetry =>
      log.info("{} fetch failed {} {}, retry", self.path.name, statusCode, message)
      injectActor ! Inject(fetchRequest.copy(retry = fetchRequest.retry + 1))
      unstashAll()
      context become standby

    case Fetched(FetchResponse(statusCode @ _, _, message, fetchRequest)) if fetchRequest.retry >= MaxRetry =>
      log.info("{} fetch failed {} {}, retry limit reached, give up", self.path.name, statusCode, message)
      context become standby

    case _ =>
      stash()
  }
}

object AbstractFetchActor extends NamedActor {
  override final val name     = "FetchActor"
  override final val poolName = "FetchActor"

  sealed trait Command
  sealed trait Event

  object Init        extends Command
  object InitSucceed extends Event
  object InitFailed  extends Event

  case class Fetch(fetchRequest: FetchRequest)     extends Command
  case class Fetched(fetchResponse: FetchResponse) extends Event

}
