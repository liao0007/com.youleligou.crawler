package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Stash}
import akka.pattern.pipe
import com.typesafe.config.Config
import com.youleligou.crawler.actors.AbstractFetchActor.{Fetch, Init, InitFailed, InitSucceed}
import com.youleligou.crawler.actors.AbstractInjectActor._
import com.youleligou.crawler.models._
import com.youleligou.crawler.services.{CacheService, HashService, InjectService}

import scala.concurrent.ExecutionContext.Implicits._

/**
  * 抓取种子注入任务,将需要抓取的任务注入到该任务中
  */
abstract class AbstractInjectActor(config: Config,
                                   cacheService: CacheService,
                                   hashService: HashService,
                                   injectService: InjectService,
                                   fetchActor: ActorRef)
    extends Actor
    with Stash
    with ActorLogging {

  override def receive: Receive = standby

  def standby: Receive = {
    case Inject(fetchRequest) =>
      val md5 = hashService.hash(fetchRequest.urlInfo.host)
      cacheService.hsetnx(AbstractInjectActor.InjectActorUrlHashKey, md5, "1").map(HashChecked) pipeTo self
      unstashAll()
      context become checkingHash(fetchRequest)
  }

  def checkingHash(fetchRequest: FetchRequest): Receive = {
    case HashChecked(true) =>
      fetchActor ! Init

    case HashChecked(false) =>
      log.warning("cache hit, ignoring")
      unstashAll()
      context become standby

    case InitSucceed =>
      fetchActor ! Fetch(fetchRequest)
      unstashAll()
      context become standby

    case InitFailed =>
      self ! Inject(fetchRequest)
      unstashAll()
      context become standby

    case _ => stash()
  }

}

object AbstractInjectActor {

  sealed trait Command
  sealed trait Event

  case class Inject(fetchRequest: FetchRequest) extends Command
  case class HashChecked(notCahced: Boolean)    extends Event

  final val InjectActorUrlHashKey: String = "InjectActorUrlHashKey"
}
