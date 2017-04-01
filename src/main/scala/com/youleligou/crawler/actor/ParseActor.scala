package com.youleligou.crawler.actor

import com.google.inject.Inject
import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actor.CountActor._
import com.youleligou.crawler.model.{FetchResult, ParseResult}
import com.youleligou.crawler.service.parse.ParseService

/**
  * Created by young.yang on 2016/8/28.
  * 解析任务
  */
class ParseActor @Inject()(config: Config, parseService: ParseService, @Named(IndexActor.name) indexActor: ActorRef)
  extends Actor
    with ActorLogging {
  private val countActor =
    context.system.actorSelection("akka://" + config.getString("crawler.appName") + "/user/" + CountActor.name)
  private val fetchDeep = config.getInt("crawler.actor.fetch.deep")

  override def receive: Receive = {
    case fetchResult: FetchResult =>
      val page: ParseResult = parseService.parse(fetchResult)
      indexActor ! page
      countActor ! ParseCounter(1)
      log.info("ParserTask send IndexerTask a index request -[" + page + "]")
      page.childLink.filter(_.deep < fetchDeep).foreach { urlInfo =>
        sender() ! urlInfo
        countActor ! ParseChildUrlCounter(1)
      }
  }
}

object ParseActor extends NamedActor {

  override final val name = "ParseActor"

}
