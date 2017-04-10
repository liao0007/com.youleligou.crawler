package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging}
import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.actors.IndexActor.Index
import com.youleligou.crawler.models.ParseResult
import com.youleligou.crawler.services.IndexService

/**
  * Created by dell on 2016/8/29.
  * 索引任务
  */
class IndexActor @Inject()(config: Config, indexService: IndexService) extends Actor with ActorLogging {

  override def receive: Receive = {
    case Index(parseResult) =>
      indexService.index(parseResult)
  }
}

object IndexActor extends NamedActor {
  override final val name     = "IndexActor"
  override final val poolName = "IndexActorPool"

  sealed trait Command
  sealed trait Event

  case class Index(parseResult: ParseResult)
}
