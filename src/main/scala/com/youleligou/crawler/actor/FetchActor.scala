package com.youleligou.crawler.actor

import com.google.inject.Inject
import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actor.CountActor._
import com.youleligou.crawler.model.UrlInfo
import com.youleligou.crawler.service.fetch.FetchService

import scala.concurrent.ExecutionContext.Implicits._

/**
  * Created by young.yang on 2016/8/28.
  * 网页抓取任务,采用Actor实现
  */
class FetchActor @Inject()(config: Config, fetchService: FetchService, @Named(ParseActor.name) parserActor: ActorRef)
  extends Actor
    with ActorLogging {
  private val countActor =
    context.system.actorSelection("akka://" + config.getString("crawler.appName") + "/user/" + CountActor.name)

  override def receive: Receive = {
    //处理抓取任务
    case page: UrlInfo =>
      log.info("Receiving fetch task: " + page)
      countActor ! FetchCounter(1)
      fetchService.fetch(page) map {
        case Some(httpResult) =>
          parserActor ! httpResult
          log.info("FetcherTask send parserTask a httpResult [" + httpResult + "]")
          countActor ! FetchOk(1)
        case _ =>
          countActor ! FetchError(1)
      }
  }
}

object FetchActor extends NamedActor {

  override final val name = "FetchActor"

}
