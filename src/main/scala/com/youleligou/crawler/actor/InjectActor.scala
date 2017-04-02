package com.youleligou.crawler.actor

import com.google.inject.Inject
import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actor.CountActor._
import com.youleligou.crawler.model._
import com.youleligou.crawler.service.cache.CacheService
import com.youleligou.crawler.service.filter.FilterService
import com.youleligou.crawler.service.hash.HashService

import scala.concurrent.ExecutionContext.Implicits._

/**
  * 抓取种子注入任务,将需要抓取的任务注入到该任务中
  */
class InjectActor @Inject()(config: Config,
                            cacheService: CacheService,
                            hashService: HashService,
                            filterService: FilterService,
                            @Named(FetchActor.poolName) fetchActor: ActorRef,
                            @Named(CountActor.poolName) countActor: ActorRef)
  extends Actor
    with ActorLogging {

  override def receive: Receive = {
    case urlInfo: UrlInfo if filterService.filter(urlInfo) =>
      // check cache
      val md5 = hashService.hash(urlInfo.url)
      cacheService.get(md5) map {
        case None =>
          log.debug("inject: " + urlInfo)
          cacheService.put(md5, "1") map {
            case true =>
              fetchActor ! urlInfo
              countActor ! InjectCounter(1)
            case false =>
              log.debug("cache failed, re-inject: " + urlInfo)
              self ! urlInfo
          }
        case _ =>
          log.debug("canceled, cache hit: " + urlInfo)
      }
  }
}

object InjectActor extends NamedActor {
  override final val name = "InjectActor"
  override final val poolName = "InjectActorPool"
}
