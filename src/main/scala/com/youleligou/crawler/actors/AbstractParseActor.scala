package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.typesafe.config.Config
import com.youleligou.crawler.actors.AbstractFetchActor.FetchResult
import com.youleligou.crawler.actors.AbstractParseActor.ParseResult
import com.youleligou.crawler.models.UrlInfo
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
      val parseResult: ParseResult = parseService.parse(fetchResult)
      parseResult.childLink.foreach(injectActor ! _)

  }
}

object AbstractParseActor extends NamedActor {
  override final val name     = "ParseActor"
  override final val poolName = "ParseActorPool"

  sealed trait Data
  case class ParseResult(urlInfo: UrlInfo,
                         title: Option[String] = None,
                         content: String,
                         publishTime: Long,
                         updateTime: Long,
                         childLink: List[UrlInfo] = List.empty[UrlInfo])
      extends Data {
    override def toString: String = "url=" + urlInfo + ",context length=" + content.length
  }
}
