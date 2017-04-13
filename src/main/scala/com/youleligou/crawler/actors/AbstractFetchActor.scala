package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern.pipe
import com.typesafe.config.Config
import com.youleligou.crawler.actors.AbstractFetchActor._
import com.youleligou.crawler.actors.AbstractInjectActor.Inject
import com.youleligou.crawler.actors.AbstractParseActor.Parse
import com.youleligou.crawler.models.{FetchRequest, FetchResponse}
import com.youleligou.crawler.modules.GuiceAkkaActorRefProvider
import com.youleligou.crawler.services.FetchService

/**
  * Created by young.yang on 2016/8/28.
  * 网页抓取任务,采用Actor实现
  */
abstract class AbstractFetchActor(config: Config, fetchService: FetchService, injectorPool: ActorRef, parserCompanion: NamedActor)
    extends Actor
    with ActorLogging
    with GuiceAkkaActorRefProvider {

  val parser: ActorRef = provideActorRef(context.system, parserCompanion, Some(context))

  import context.dispatcher

  final val MaxRetry: Int = config.getInt("crawler.fetch.max-retry")

  override def receive: Receive = {
    case Fetch(fetchRequest) =>
      fetchService.fetch(fetchRequest).map(Fetched) pipeTo self
      context become fetching(sender)
  }

  def fetching(injector: ActorRef): Receive = {
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
      log.warning("{} fetch failed {} {}", self.path, statusCode, message)
      injector ! WorkFinished
      context.system.terminate()

    case Fetched(FetchResponse(statusCode @ _, _, message, fetchRequest)) if fetchRequest.retry < MaxRetry =>
      log.info("{} fetch failed {} {}, retry", self.path, statusCode, message)
      injectorPool ! Inject(fetchRequest.copy(retry = fetchRequest.retry + 1), force = true)
      injector ! WorkFinished
      context unbecome ()

    case Fetched(FetchResponse(statusCode @ _, _, message, fetchRequest)) if fetchRequest.retry >= MaxRetry =>
      log.warning("{} fetch failed {} {}, retry limit reached, give up", self.path, statusCode, message)
      injector ! WorkFinished
      context unbecome ()

  }
}

object AbstractFetchActor extends NamedActor {
  override final val name     = "FetchActor"
  override final val poolName = "FetchActorPool"

  sealed trait Command
  sealed trait Event

  case class Fetch(fetchRequest: FetchRequest)     extends Command
  case class Fetched(fetchResponse: FetchResponse) extends Event

  case object WorkFinished extends Event

}
