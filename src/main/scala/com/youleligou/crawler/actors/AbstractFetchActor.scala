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
import com.youleligou.crawler.modules.GuiceAkkaActorRefProvider
import com.youleligou.crawler.services.FetchService

/**
  * Created by young.yang on 2016/8/28.
  * 网页抓取任务,采用Actor实现
  */
abstract class AbstractFetchActor(config: Config,
                                  fetchService: FetchService,
                                  injectorPool: ActorRef,
                                  proxyAssistantPool: ActorRef,
                                  parserCompanion: NamedActor)
    extends Actor
    with ActorLogging
    with GuiceAkkaActorRefProvider {

  val parser: ActorRef = provideActorRef(context.system, parserCompanion, Some(context))

  import context.dispatcher

  final val Retry    = config.getInt("crawler.fetch.retry")
  final val useProxy = config.getBoolean("crawler.fetch.useProxy")

  override def receive: Receive = standby

  def standby: Receive = {
    case InitProxyServer =>
      if (useProxy) {
        proxyAssistantPool ! GetProxyServer
        context become (initializing(sender), discardOld = false)
      } else {
        self ! ByPassProxyServer
        context become (initializing(sender), discardOld = false)
      }

  }

  def initializing(injector: ActorRef): Receive = {
    case ProxyServerAvailable(server) =>
      injector ! InitProxyServerSucceed
      context become (proxyServerAvailable(Some(server)), discardOld = false)

    case ByPassProxyServer =>
      injector ! InitProxyServerSucceed
      context become (proxyServerAvailable(None), discardOld = false)

    case ProxyServerUnavailable =>
      injector ! InitProxyServerFailed
      context unbecome ()

    case Fetched(fetchResponse @ FetchResponse(FetchService.Ok, _, _, _)) =>
      log.info("{} fetch succeed", self.path)
      parser ! Parse(fetchResponse)
      injector ! WorkFinished
      context unbecome ()

    case Fetched(FetchResponse(statusCode @ FetchService.NotFound, _, message, _)) =>
      log.info("{} fetch failed {} {}", self.path, statusCode, message)
      injector ! WorkFinished
      context unbecome ()

    case Fetched(FetchResponse(statusCode @ FetchService.PaymentRequired, _, message, _)) =>
      log.info("{} fetch failed {} {}", self.path, statusCode, message)
      injector ! WorkFinished
      context.system.terminate()

    case Fetched(FetchResponse(statusCode @ _, _, message, fetchRequest)) if fetchRequest.retry < Retry =>
      log.info("{} fetch failed {} {}, retry", self.path, statusCode, message)
      injectorPool ! Inject(fetchRequest.copy(retry = fetchRequest.retry + 1), force = true)
      injector ! WorkFinished
      context unbecome ()

    case Fetched(FetchResponse(statusCode @ _, _, message, fetchRequest)) if fetchRequest.retry >= Retry =>
      log.info("{} fetch failed {} {}, retry limit reached, give up", self.path, statusCode, message)
      injector ! WorkFinished
      context unbecome ()

  }

  def proxyServerAvailable(proxyServerOpt: Option[CrawlerProxyServer]): Receive = {
    case Fetch(fetchRequest) =>
      fetchService.fetch(fetchRequest, proxyServerOpt).map(Fetched) pipeTo self
      context unbecome ()
  }
}

object AbstractFetchActor extends NamedActor {
  override final val name     = "FetchActor"
  override final val poolName = "FetchActorPool"

  sealed trait Command
  sealed trait Event

  object InitProxyServer        extends Command
  object ByPassProxyServer      extends Command
  object InitProxyServerSucceed extends Event
  object InitProxyServerFailed  extends Event

  case class Fetch(fetchRequest: FetchRequest)     extends Command
  case class Fetched(fetchResponse: FetchResponse) extends Event

  case object WorkFinished extends Event

}
