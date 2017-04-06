package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Stash}
import akka.pattern.pipe
import com.typesafe.config.Config
import com.youleligou.crawler.actors.AbstractFetchActor.Fetch
import com.youleligou.crawler.actors.AbstractInjectActor.{GenerateFetch, HashNxResult, SeedInitialized}
import com.youleligou.crawler.actors.CountActor._
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
                                   countActor: ActorRef)
    extends Actor
    with Stash
    with ActorLogging {

  var seed: Int = 0

  override def preStart(): Unit = {
    super.preStart()
    injectService.initSeed() pipeTo self
  }

  override def receive: Receive = {
    case SeedInitialized(initSeed) =>
      log.info("seed initialized with: " + initSeed + ", scheduling GenerateFetch")
      seed = initSeed
      unstashAll()
      context.become(initialized)
    case _ =>
      log.info("not initialized, stashing")
      stash()
  }

  def initialized: Receive = {
    case GenerateFetch =>
      log.info("generating fetch")
      seed = seed + 1
      self ! injectService.generateFetch(seed)

    case fetch @ Fetch(_, urlInfo: UrlInfo) if filterService.filter(urlInfo) =>
      val md5 = hashService.hash(urlInfo.url)
      log.info("querying hashing info: " + md5)
      cacheService.hsetnx(AbstractInjectActor.InjectActorUrlHash, md5, "1", fetch) pipeTo self
      unstashAll()
      context.become(queryingHash)

    case _ =>
      log.info("still querying hashing info")
      stash()
  }

  def queryingHash: Receive = {
    case HashNxResult(fetch, true) =>
      log.info("not cached, now fetching: " + fetch.urlInfo)
      fetchActor ! fetch
      countActor ! InjectCounter(1)
      unstashAll()
      context.become(initialized)

    case HashNxResult(fetch, false) =>
      log.info("cache hit: " + fetch.urlInfo)
      unstashAll()
      context.become(initialized)

    case _ =>
      log.info("still querying hashing info")
      stash()
  }

}

object AbstractInjectActor {

  sealed trait InjectActorMessage
  case class SeedInitialized(seed: Int)                      extends InjectActorMessage
  case object GenerateFetch                                  extends InjectActorMessage
  case class HashNxResult(fetch: Fetch, successful: Boolean) extends InjectActorMessage

  final val InjectActorUrlHash: String = "InjectActorUrlHash"
}
