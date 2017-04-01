package com.youleligou.crawler.actor

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actor.CountActor.IndexCounter
import com.youleligou.crawler.model.ParseResult
import com.youleligou.crawler.service.index.IndexService

/**
  * Created by dell on 2016/8/29.
  * 索引任务
  */
class IndexActor @Inject()(config: Config, indexService: IndexService, @Named(CountActor.poolName) countActor: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case page: ParseResult =>
      log.info("index url: " + page.url)
      indexService.index(page)
      countActor ! IndexCounter(1)
  }
}

object IndexActor extends NamedActor {
  override final val name = "IndexActor"
  override final val poolName = "IndexActorPool"
}
