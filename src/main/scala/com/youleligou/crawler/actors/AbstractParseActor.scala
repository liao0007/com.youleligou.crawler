package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.typesafe.config.Config
import com.youleligou.crawler.actors.AbstractInjectActor.Inject
import com.youleligou.crawler.actors.AbstractParseActor.Parse
import com.youleligou.crawler.models.{FetchResponse, ParseResult}
import com.youleligou.crawler.services.ParseService

/**
  * Created by young.yang on 2016/8/28.
  * 解析任务
  */
abstract class AbstractParseActor(config: Config, parseService: ParseService, indexerPool: ActorRef, injectorPool: ActorRef)
    extends Actor
    with ActorLogging {

  override def receive: Receive = {
    case Parse(fetchResponse) =>
      log.debug("{} parse {}", self.path, fetchResponse.fetchRequest.urlInfo)
      val parseResult: ParseResult = parseService.parse(fetchResponse)
      parseResult.childLink.foreach { urlInfo =>
        injectorPool ! Inject(parseResult.fetchResponse.fetchRequest.copy(urlInfo = urlInfo, retry = 0))
      }
  }
}

object AbstractParseActor extends NamedActor {
  override final val name     = "ParseActor"
  override final val poolName = "ParseActorPool"

  sealed trait Command
  sealed trait Event

  case class Parse(fetchResponse: FetchResponse) extends Command
}
