package com.youleligou.crawler.actor

import com.google.inject.Inject
import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actor.CountActor._
import com.youleligou.crawler.model.UrlInfo
import com.youleligou.crawler.service.fetch.FetchService

import scala.concurrent.ExecutionContext.Implicits._
import scala.util.{Failure, Success}

/**
  * Created by young.yang on 2016/8/28.
  * 网页抓取任务,采用Actor实现
  */
class FetchActor @Inject()(config: Config, fetchService: FetchService, @Named(ParseActor.poolName) parserActor: ActorRef, @Named(CountActor.poolName) countActor: ActorRef)
  extends Actor
    with ActorLogging {

  override def receive: Receive = {
    //处理抓取任务
    case page: UrlInfo =>
      log.info("fetch url: " + page)
      countActor ! FetchCounter(1)
      fetchService.fetch(page) onComplete {
        case Success(fetchResult) =>
          parserActor ! fetchResult
          countActor ! FetchOk(1)
        case Failure(_) =>
          countActor ! FetchError(1)
      }
  }
}

object FetchActor extends NamedActor {
  override final val name = "FetchActor"
  override final val poolName = "FetchActorPool"
}
