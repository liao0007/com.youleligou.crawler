package com.youleligou.crawler.actors

import javax.inject.Named

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern.pipe
import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.actors.Parser.Parse
import com.youleligou.crawler.models.{FetchRequest, FetchResponse}
import com.youleligou.crawler.modules.GuiceAkkaActorRefProvider
import com.youleligou.crawler.services.FetchService

/**
  * Created by young.yang on 2016/8/28.
  * 网页抓取任务,采用Actor实现
  */
class Fetcher @Inject()(config: Config, fetchService: FetchService, @Named(Injector.PoolName) injectors: ActorRef)
    extends Actor
    with ActorLogging
    with GuiceAkkaActorRefProvider {

  import context.dispatcher

  final val MaxRetry: Int = config.getInt("crawler.fetch.maxRetry")

  val parsers: ActorRef = provideActorRef(context.system, Parser, Some(context))

  override def receive: Receive = {
    case Fetcher.Fetch(fetchRequest) =>
      fetchService.fetch(fetchRequest).map(Fetcher.Fetched) pipeTo self
      context become fetching(sender)
  }

  def fetching(injector: ActorRef): Receive = {
    case Fetcher.Fetched(fetchResponse @ FetchResponse(FetchService.Ok, _, _, _)) =>
      log.debug("{} fetch succeed", self.path)
      parsers ! Parse(fetchResponse)
      injector ! Fetcher.WorkFinished
      context unbecome ()

    case Fetcher.Fetched(FetchResponse(statusCode @ FetchService.PaymentRequired, _, message, _)) =>
      log.warning("{} fetch failed {} {}", self.path, statusCode, message)
      injector ! Fetcher.WorkFinished
      context.system.terminate()

    case Fetcher.Fetched(FetchResponse(statusCode @ _, _, message, fetchRequest)) if fetchRequest.retry < MaxRetry =>
      log.debug("{} fetch failed {} {}, retry", self.path, statusCode, message)
      injectors ! Injector.Inject(fetchRequest.copy(retry = fetchRequest.retry + 1), force = true)
      injector ! Fetcher.WorkFinished
      context unbecome ()

    case Fetcher.Fetched(FetchResponse(statusCode @ _, _, message, fetchRequest)) if fetchRequest.retry >= MaxRetry =>
      log.warning("{} fetch failed {} {}, retry limit reached, give up", self.path, statusCode, message)
      injector ! Fetcher.WorkFinished
      context unbecome ()

  }
}

object Fetcher extends NamedActor {
  final val Name     = "FetchActor"
  final val PoolName = "FetchActorPool"

  sealed trait Command
  sealed trait Event

  case class Fetch(fetchRequest: FetchRequest)     extends Command
  case class Fetched(fetchResponse: FetchResponse) extends Event

  case object WorkFinished extends Event

}
