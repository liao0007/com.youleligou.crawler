package com.youleligou.crawler.actor

import com.google.inject.Inject
import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actor.CountActor._
import com.youleligou.crawler.actor.FetchActor.Fetch
import com.youleligou.crawler.actor.InjectActor.{GenerateFetch, Init}
import com.youleligou.crawler.model._
import com.youleligou.crawler.service.{CacheService, FilterService, HashService, InjectService}
import com.youleligou.eleme.RestaurantInjectService

import scala.concurrent.ExecutionContext.Implicits._
import akka.actor.Stash
import akka.pattern.pipe

import scala.concurrent.duration.{FiniteDuration, MILLISECONDS}

/**
  * 抓取种子注入任务,将需要抓取的任务注入到该任务中
  */
class InjectActor @Inject()(config: Config,
                            cacheService: CacheService,
                            hashService: HashService,
                            filterService: FilterService,
                            @Named(RestaurantInjectService.name) injectService: InjectService,
                            @Named(FetchActor.poolName) fetchActor: ActorRef,
                            @Named(CountActor.poolName) countActor: ActorRef)
  extends Actor
    with Stash
    with ActorLogging {

  var seed: Int = 0

  override def preStart(): Unit = {
    injectService.initSeed().pipeTo(self)
  }

  override def postRestart(reason: Throwable): Unit = {
    preStart()
  }

  override def receive: Receive = {

    case Init =>

    case initSeed: Int =>
      log.info("seed inited with: " + initSeed + ", scheduling GenerateFetch")
      seed = initSeed
      context.system.scheduler.schedule(FiniteDuration(300, MILLISECONDS), FiniteDuration(300, MILLISECONDS), self, GenerateFetch)
      context.become(initialized)
    case _ =>
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

object InjectActor extends NamedActor {
  override final val name = "InjectActor"
  override final val poolName = "InjectActorPool"

  case object Init

  case object GenerateFetch
}
