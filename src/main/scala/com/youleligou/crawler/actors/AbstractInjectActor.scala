package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Stash}
import akka.pattern.pipe
import com.typesafe.config.Config
import com.youleligou.crawler.actors.AbstractFetchActor.FetchUrl
import com.youleligou.crawler.actors.AbstractInjectActor.{GenerateFetch, HashCheckResult, Init, Initialized}
import com.youleligou.crawler.models._
import com.youleligou.crawler.services.{CacheService, FilterService, HashService, InjectService}

import scala.concurrent.ExecutionContext.Implicits._

/**
  * 抓取种子注入任务,将需要抓取的任务注入到该任务中
  */
abstract class AbstractInjectActor(config: Config,
                                   cacheService: CacheService,
                                   hashService: HashService,
                                   filterService: FilterService,
                                   injectService: InjectService,
                                   fetchActor: ActorRef,
                                   proxyAssistantActor: ProxyAssistantActor,
                                   countActor: ActorRef)
    extends Actor
    with Stash
    with ActorLogging {

  override def receive: Receive = standby

  def standby: Receive = {
    case Init =>
      injectService.initSeed().map(Initialized) pipeTo self

    case Initialized(seed) =>
      unstashAll()
      context become active(seed)

    case _ =>
      stash()
  }

  def active(seed: Int): Receive = {
    case GenerateFetch =>
      self ! injectService.generateFetch(seed)
      unstashAll()
      context become active(seed + 1)

    case fetch @ FetchUrl(_, urlInfo: UrlInfo) if filterService.filter(urlInfo) =>
      val md5 = hashService.hash(urlInfo.url)
      cacheService.hsetnx(AbstractInjectActor.InjectActorUrlHashKey, md5, "1").map(HashCheckResult(_, fetch)) pipeTo self

    case HashCheckResult(true, fetch) =>
      fetchActor ! fetch
  }
}

object AbstractInjectActor {

  sealed trait Event
  case object Init          extends Event
  case object GenerateFetch extends Event

  sealed trait Data
  case class Initialized(seed: Int)                                  extends Event
  case class HashCheckResult(isDuplicated: Boolean, fetch: FetchUrl) extends Event

  final val InjectActorUrlHashKey: String = "InjectActorUrlHashKey"
}
