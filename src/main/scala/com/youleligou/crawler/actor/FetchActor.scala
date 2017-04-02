package com.youleligou.crawler.actor

import com.google.inject.Inject
import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actor.CountActor._
import com.youleligou.crawler.model.UrlInfo
import com.youleligou.crawler.service.FetchService
import com.youleligou.crawler.service.FetchService.FetchException

import scala.concurrent.ExecutionContext.Implicits._
import scala.util.{Failure, Success}

/**
  * Created by young.yang on 2016/8/28.
  * 网页抓取任务,采用Actor实现
  */
class FetchActor @Inject()(config: Config,
                           fetchService: FetchService,
                           @Named(ParseActor.poolName) parserActor: ActorRef,
                           @Named(CountActor.poolName) countActor: ActorRef)
  extends Actor
    with ActorLogging {

  var sleepInterval = 100 //millis
  var retry = 1
  val maxRetry = 20

  override def receive: Receive = {
    //处理抓取任务
    case urlInfo: UrlInfo =>
      log.info("fetch: " + urlInfo)
      countActor ! FetchCounter(1)
      fetchService.fetch(urlInfo) onComplete {
        case Success(fetchResult) =>
          sleepInterval = 100
          retry = 1
          log.info("fetch success: " + urlInfo.url)
          parserActor ! fetchResult
          countActor ! FetchOk(1)
        case Failure(FetchException(statusCode, message)) if retry < maxRetry =>
          retry = retry + 1
          statusCode match {
            case FetchService.PaymentRequired | FetchService.NotFound =>
              log.error("fetch failed: " + statusCode + " " + message + "cancel fetch")

            case FetchService.TooManyRequest =>
              log.info("proxy too many request, re-fetch: " + urlInfo.url + " in seconds: " + sleepInterval / 1000.0)
              sleepInterval = sleepInterval * 2
              Thread.sleep(sleepInterval)
              self ! urlInfo
            case _ =>
              sleepInterval = 100
              log.info("fetch failed: " + statusCode + " " + message + "re-fetch")
              self ! urlInfo
          }
          countActor ! FetchError(1)
        case _ =>
          retry = 1
          sleepInterval = 100
          log.info("fetch failed with retry limit: " + urlInfo.url)
      }
  }
}

object FetchActor extends NamedActor {
  override final val name = "FetchActor"
  override final val poolName = "FetchActorPool"
}
