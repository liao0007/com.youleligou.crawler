package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Stash}
import com.typesafe.config.Config
import com.youleligou.crawler.actors.AbstractFetchActor.{Fetch, Init, InitFailed, InitSucceed}
import com.youleligou.crawler.actors.AbstractInjectActor._
import com.youleligou.crawler.models._
import com.youleligou.crawler.services.HashService
import play.api.libs.json.Json
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * 抓取种子注入任务,将需要抓取的任务注入到该任务中
  */
abstract class AbstractInjectActor(config: Config, redisClient: RedisClient, hashService: HashService, fetchActor: ActorRef)
    extends Actor
    with Stash
    with ActorLogging {

  override def receive: Receive = standby

  def standby: Receive = {
    case Inject(fetchRequest) =>
      log.info("{} hash check {}", self.path.name, fetchRequest)
      val md5 = hashService.hash(fetchRequest.urlInfo.url)
      redisClient.hsetnx(AbstractInjectActor.InjectActorInjectedUrlHashKey, md5, "1") flatMap {
        case true =>
          log.info("{} injected {}", self.path.name, fetchRequest)
          redisClient.lpush(InjectActorPendingUrlQueueKey, Json.toJson(fetchRequest).toString())
        case _ =>
          log.info("{} rejected {}", self.path.name, fetchRequest)
          Future.successful(0L)
      } recover {
        case NonFatal(x) =>
          log.warning(x.getMessage)
          0L
      }

    case Tick =>
      log.info("{} tick", self.path.name)
      redisClient.rpop[String](InjectActorPendingUrlQueueKey) map {
        case Some(fetchRequestString) =>
          Json.parse(fetchRequestString).validate[FetchRequest].asOpt
        case _ =>
          None
      } map {
        case Some(fetchRequest) =>
          fetchActor ! Init
          context become fetching(fetchRequest)
        case _ =>
      } recover {
        case NonFatal(x) =>
          log.warning(x.getMessage)
      }
  }

  def fetching(fetchRequest: FetchRequest): Receive = {
    case InitSucceed =>
      log.info("{} fetch init succeed {}", self.path.name, fetchRequest)
      sender ! Fetch(fetchRequest)
      unstashAll()
      context become standby

    case InitFailed =>
      log.info("{} fetch init failed {}", self.path.name, fetchRequest)
      self ! Inject(fetchRequest)
      unstashAll()
      context become standby

    case Tick =>
    //ignore ticks

    case _ => stash()
  }

}

object AbstractInjectActor {

  sealed trait Command
  sealed trait Event

  case class Inject(fetchRequest: FetchRequest) extends Command
  case object Tick                              extends Command

  final val InjectActorPendingUrlQueueKey: String = "InjectActorPendingUrlQueueKey"
  final val InjectActorInjectedUrlHashKey: String = "InjectActorInjectedUrlHashKey"
}
