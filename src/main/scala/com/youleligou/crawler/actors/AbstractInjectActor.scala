package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Stash}
import akka.pattern.pipe
import com.typesafe.config.Config
import com.youleligou.crawler.actors.AbstractFetchActor._
import com.youleligou.crawler.actors.AbstractInjectActor._
import com.youleligou.crawler.models._
import com.youleligou.crawler.modules.GuiceAkkaActorRefProvider
import com.youleligou.crawler.services.HashService
import play.api.libs.json.Json
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * 抓取种子注入任务,将需要抓取的任务注入到该任务中
  */
abstract class AbstractInjectActor(config: Config, redisClient: RedisClient, hashService: HashService, fetcherCompanion: NamedActor)
    extends Actor
    with Stash
    with ActorLogging
    with GuiceAkkaActorRefProvider {

  val CachePrefix: String
  lazy val PendingInjectingUrlQueueKey: String = AbstractInjectActor.pendingInjectingUrlQueueKey(CachePrefix)
  lazy val InjectedUrlHashKey: String          = AbstractInjectActor.injectedUrlHashKey(CachePrefix)

  val fetcher: ActorRef = provideActorRef(context.system, fetcherCompanion, Some(context))

  override def receive: Receive = standby

  def standby: Receive = {
    case ClearCache =>
      redisClient.del(PendingInjectingUrlQueueKey, InjectedUrlHashKey).map(CacheCleared) pipeTo sender

    case Inject(fetchRequest, force) =>
      log.info("{} hash check {}", self.path, fetchRequest)
      val md5 = hashService.hash(fetchRequest.urlInfo.url)

      redisClient.hsetnx(InjectedUrlHashKey, md5, "1") map { result =>
        if (force) true else result
      } flatMap {
        case true =>
          log.info("{} injected {}", self.path, fetchRequest)
          redisClient.lpush(PendingInjectingUrlQueueKey, Json.toJson(fetchRequest).toString())
        case _ =>
          log.info("{} rejected {}", self.path, fetchRequest)
          Future.successful(0L)
      } recover {
        case NonFatal(x) =>
          log.warning(x.getMessage)
          0L
      } map Injected pipeTo self

    case Injected(count) =>
    case Tick =>
      log.info("{} tick", self.path)
      redisClient.rpop[String](PendingInjectingUrlQueueKey) recover {
        case NonFatal(x) =>
          log.warning(x.getMessage)
          None
      } pipeTo self

    case Some(fetchRequestString: String) =>
      Json.parse(fetchRequestString).validate[FetchRequest].asOpt match {
        case Some(fetchRequest) =>
          fetcher ! InitProxyServer
          context become (fetching(fetchRequest), discardOld = false)
        case _ =>
      }

    case _ =>
  }

  def fetching(fetchRequest: FetchRequest): Receive = {
    case InitProxyServerSucceed =>
      log.info("{} fetch init succeed {}", self.path, fetchRequest)
      sender ! Fetch(fetchRequest)

    case InitProxyServerFailed =>
      log.info("{} fetch init failed {}", self.path, fetchRequest)
      self ! Inject(fetchRequest)
      unstashAll()
      context unbecome ()

    case WorkFinished =>
      unstashAll()
      context unbecome ()

    case Tick => //ignore ticks

    case _ => stash()
  }

}

object AbstractInjectActor {
  def pendingInjectingUrlQueueKey(cachePrefix: String): String = cachePrefix + "PendingInjectingUrlQueue"
  def injectedUrlHashKey(cachePrefix: String): String          = cachePrefix + "InjectedUrlHash"

  sealed trait Command
  sealed trait Event

  case object ClearCache               extends Command
  case class CacheCleared(count: Long) extends Event

  case class Inject(fetchRequest: FetchRequest, force: Boolean = false) extends Command
  case class Injected(count: Long)                                      extends Event

  case object Tick extends Command
}
