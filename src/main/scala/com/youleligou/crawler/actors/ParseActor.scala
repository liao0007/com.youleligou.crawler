package com.youleligou.crawler.actors

import javax.inject.Inject

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.typesafe.config.Config
import com.youleligou.crawler.actors.CountActor._
import com.youleligou.crawler.parsers.Parser
import com.youleligou.crawler.models.{ParseResult, FetchResult}

/**
  * Created by young.yang on 2016/8/28.
  * 解析任务
  */
class ParseActor @Inject()(config: Config)(parser: Parser, indexTask: ActorRef) extends Actor with ActorLogging {
  private val countActor =
    context.system.actorSelection("akka://" + config.getString("crawler.appName") + "/user/" + config.getString("crawler.counter.name"))
  private val fetchDeep = config.getInt("crawler.fetch.deep")

  override def receive: Receive = {
    case fetchResult: FetchResult =>
      val page: ParseResult = parser.parse(fetchResult)
      indexTask ! page
      countActor ! ParseCounter(1)
      log.info("ParserTask send IndexerTask a index request -[" + page + "]")
      page.childLink.filter(_.deep < fetchDeep).foreach { urlInfo =>
        sender() ! urlInfo
        countActor ! ParseChildUrlCounter(1)
      }
  }
}

object ParseActor {
  def props(parser: Parser, indexerActor: ActorRef) = Props(classOf[ParseActor], parser, indexerActor)
}
