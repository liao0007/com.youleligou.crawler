package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Stash}
import akka.pattern.pipe
import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.core.modules.GuiceAkkaActorRefProvider
import com.youleligou.crawler.actors.Fetcher._
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
class Injector @Inject()(config: Config, redisClient: RedisClient, hashService: HashService)
    extends Actor
    with Stash
    with ActorLogging
    with GuiceAkkaActorRefProvider {

  val fetcher: ActorRef = provideActorRef(context.system, Fetcher, Some(context))

  override def receive: Receive = standby

  def standby: Receive = {
    case Injector.ClearCache(queue) =>
      redisClient.del(pendingInjectingUrlQueueKey(queue), injectedUrlHashKey(queue)).map(Injector.CacheCleared) pipeTo sender

    case Injector.Inject(fetchRequest, force) =>
      log.debug("{} hash check {}", self.path, fetchRequest)
      val md5   = hashService.hash(fetchRequest.urlInfo.toString)
      val queue = fetchRequest.urlInfo.jobType

      redisClient.hsetnx(injectedUrlHashKey(queue), md5, "1") map { result =>
        if (force) true else result
      } flatMap {
        case true =>
          log.debug("{} injected {}", self.path, fetchRequest)
          redisClient.lpush(pendingInjectingUrlQueueKey(queue), Json.toJson(fetchRequest).toString())
        case _ =>
          log.debug("{} rejected {}", self.path, fetchRequest)
          Future.successful(0L)
      } recover {
        case NonFatal(x) =>
          log.warning(x.getMessage)
          0L
      } map Injector.Injected pipeTo self

    case Injector.Injected(count) =>
    case Injector.Tick(queue) =>
      log.debug("{} tick {}", self.path, queue)
      redisClient.rpop[String](pendingInjectingUrlQueueKey(queue)).map(Injector.Ticked) recover {
        case NonFatal(x) =>
          log.warning(x.getMessage)
          None
      } pipeTo self

    case Injector.Ticked(Some(fetchRequestString)) =>
      Json.parse(fetchRequestString).validate[FetchRequest].asOpt match {
        case Some(fetchRequest) =>
          fetcher ! Fetch(fetchRequest)
          context become (fetching(fetcher), discardOld = false)
        case _ =>
      }

    case _ =>
  }

  def fetching(fetcher: ActorRef): Receive = {
    case WorkFinished =>
      unstashAll()
      context unbecome ()

    case Injector.Tick => //ignore ticks

    case _ => stash()
  }

  private def pendingInjectingUrlQueueKey(cachePrefix: String): String = cachePrefix + "-PendingInjectingUrlQueue"
  private def injectedUrlHashKey(cachePrefix: String): String          = cachePrefix + "-InjectedUrlHash"
}

object Injector extends NamedActor {
  final val Name     = "InjectActor"
  final val PoolName = "InjectActorPool"

  sealed trait Command
  sealed trait Event

  case class ClearCache(queue: String) extends Command
  case class CacheCleared(count: Long) extends Event

  case class Inject(fetchRequest: FetchRequest, force: Boolean = false) extends Command
  case class Injected(count: Long)                                      extends Event

  case class Tick(queue: String)                        extends Command
  case class Ticked(fetchRequestString: Option[String]) extends Event

}
