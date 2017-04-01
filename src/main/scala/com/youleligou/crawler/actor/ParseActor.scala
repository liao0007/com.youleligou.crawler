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
class ParseActor @Inject()(config: Config,
                           parseService: ParseService,
                           @Named(IndexActor.poolName) indexActor: ActorRef,
                           @Named(CountActor.poolName) countActor: ActorRef)
  extends Actor
    with ActorLogging {

  private val fetchDeep = config.getInt("crawler.actor.fetch.deep")

  override def receive: Receive = {
    case fetchResult: FetchResult =>
      log.info("parse url: " + fetchResult.url)
      val page: ParseResult = parseService.parse(fetchResult)
      indexActor ! page
      countActor ! ParseCounter(1)
      page.childLink.filter(urlInfo => urlInfo.deep < fetchDeep && urlInfo.url.startsWith(urlInfo.domain)).foreach { urlInfo =>
        sender() ! urlInfo
        countActor ! ParseChildUrlCounter(1)
      }
  }
}

object ParseActor extends NamedActor {
  override final val name = "ParseActor"
  override final val poolName = "ParseActorPool"
}
