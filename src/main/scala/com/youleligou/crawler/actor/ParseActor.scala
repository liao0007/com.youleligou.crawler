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
                           @Named(InjectActor.poolName) injectActor: ActorRef,
                           @Named(CountActor.poolName) countActor: ActorRef)
  extends Actor
    with ActorLogging {
  override def receive: Receive = {
    case fetchResult: FetchResult =>
      log.debug("parse: " + fetchResult.urlInfo)
      val parseResult: ParseResult = parseService.parse(fetchResult)
      indexActor ! parseResult
      countActor ! ParseCounter(1)
      parseResult.childLink.foreach { urlInfo =>
        injectActor ! urlInfo
        countActor ! ParseChildUrlCounter(1)
      }
  }
}

object ParseActor extends NamedActor {
  override final val name = "ParseActor"
  override final val poolName = "ParseActorPool"
}
