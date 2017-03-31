package com.youleligou.crawler.spider.actors

import javax.inject.Inject

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.typesafe.config.Config
import com.youleligou.crawler.spider.actors.CountActor._
import com.youleligou.crawler.spider.parser.Parser
import com.youleligou.models.{HttpPage, HttpResult}

/**
  * Created by young.yang on 2016/8/28.
  * 解析任务
  */
class ParseActor @Inject()(config: Config)(parser: Parser, indexTask: ActorRef) extends Actor with ActorLogging {
  private val countActor =
    context.system.actorSelection("akka://" + config.getString("crawler.appName") + "/user/" + config.getString("crawler.counter.name"))

  private val fetchDeep = config.getInt("crawler.fetcher.deep")

  private var fetcher: ActorRef = null

  override def receive: Receive = {
    case httpResult: HttpResult =>
      fetcher = sender()
      val page: HttpPage = parser.parse(httpResult)
      indexTask ! page
      countActor ! ParseCounter(1)
      log.info("ParserTask send IndexerTask a index request -[" + page + "]")
      val childLinks = page.getChildLink
      if (childLinks._2 < fetchDeep) {
        fetcher ! childLinks._1
        countActor ! ParseChildUrlCounter(childLinks._1.size)
      } else {
        log.info("fetch deep size now  is -[" + childLinks._2 + "] remove urls size -[" + childLinks._1.size + "]")
      }
  }
}

object ParseActor {
  def props(parser: Parser, indexerActor: ActorRef) = Props(classOf[ParseActor], parser, indexerActor)
}
