package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.AbstractParseActor.ParseResult
import com.youleligou.crawler.services.IndexService

/**
  * Created by dell on 2016/8/29.
  * 索引任务
  */
class IndexActor @Inject()(config: Config, indexService: IndexService, @Named(CountActor.poolName) countActor: ActorRef)
    extends Actor
    with ActorLogging {

  override def receive: Receive = {
    case parsedResult: ParseResult =>
      indexService.index(parsedResult)
  }
}

object IndexActor extends NamedActor {
  override final val name     = "IndexActor"
  override final val poolName = "IndexActorPool"
}
