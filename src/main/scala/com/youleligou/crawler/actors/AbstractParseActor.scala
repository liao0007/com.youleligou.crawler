package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.typesafe.config.Config
import com.youleligou.crawler.actors.CountActor._
import com.youleligou.crawler.models.{FetchResult, ParseResult}
import com.youleligou.crawler.services.ParseService

/**
  * Created by young.yang on 2016/8/28.
  * 解析任务
  */
abstract class AbstractParseActor(config: Config, parseService: ParseService, indexActor: ActorRef, injectActor: ActorRef, countActor: ActorRef)
  extends Actor
    with ActorLogging {

  override def receive: Receive = {

    case fetchResult: FetchResult =>
      log.info("parse: " + fetchResult.urlInfo)
      val parseResult: ParseResult = parseService.parse(fetchResult)
      indexActor ! parseResult
      countActor ! ParseCounter(1)
      parseResult.childLink.foreach { urlInfo =>
        injectActor ! urlInfo
        countActor ! ParseChildUrlCounter(1)
      }

  }
}
