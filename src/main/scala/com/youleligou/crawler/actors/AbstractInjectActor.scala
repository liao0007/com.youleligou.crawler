package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Stash}
import akka.pattern.pipe
import com.typesafe.config.Config
import com.youleligou.crawler.actors.AbstractFetchActor.Fetch
import com.youleligou.crawler.actors.AbstractInjectActor.{GenerateFetch, SeedInitialized}
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
    pipe(injectService.initSeed()).to(self)
  }

  override def receive: Receive = {
    case SeedInitialized(initSeed) =>
      log.info("seed initialized with: " + initSeed + ", scheduling GenerateFetch")
      seed = initSeed
      unstashAll()
      context.become(initialized)
    case _ =>
      stash()
  }

  def initialized: Receive = {
    case GenerateFetch =>
      seed = seed + 1
      self ! injectService.generateFetch(seed)

    case fetch@Fetch(_, urlInfo: UrlInfo) if filterService.filter(urlInfo) =>
      // check cache
      val md5 = hashService.hash(urlInfo.url)
      cacheService.get(md5) map {
        case None =>
          log.info("inject: " + urlInfo)
          cacheService.put(md5, "1") map {
            case true =>
              fetchActor ! fetch
              countActor ! InjectCounter(1)
            case false =>
              log.info("cache failed, re-inject: " + urlInfo)
              self ! urlInfo
          }
        case _ =>
          log.info("cache hit: " + urlInfo)
      }
  }
}

object AbstractInjectActor {

  case class SeedInitialized(seed: Int)
  case object GenerateFetch
}
