package com.youleligou.crawler.actors

import javax.inject.Inject

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.CountActor._
import com.youleligou.crawler.fetchers.Fetcher
import com.youleligou.crawler.models.UrlInfo

import scala.concurrent.ExecutionContext.Implicits._

/**
  * Created by young.yang on 2016/8/28.
  * 网页抓取任务,采用Actor实现
  */
class FetchActor @Inject()(config: Config, fetcher: Fetcher, @Named(ParseActor.name) parserActor: ActorRef) extends Actor with ActorLogging {
  private val countActor =
    context.system.actorSelection("akka://" + config.getString("crawler.appName") + "/user/" + config.getString("crawler.counter.name"))

  override def receive: Receive = {
    //处理抓取任务
    case page: UrlInfo =>
      countActor ! FetchCounter(1)
      fetcher.fetch(page) map {
        case Some(httpResult) =>
          parserActor ! httpResult
          log.info("FetcherTask send parserTask a httpResult [" + httpResult + "]")
          countActor ! FetchOk(1)
        case _ =>
          countActor ! FetchError(1)
      }
    //将解析完成的子url发送到注入任务继续抓取
    case urls: List[UrlInfo] => sender() ! urls
  }
}

object FetchActor extends NamedActor {

  override final val name = "FetchActor"

}
