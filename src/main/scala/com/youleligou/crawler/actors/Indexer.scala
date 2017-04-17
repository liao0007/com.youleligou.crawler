package com.youleligou.crawler.actors

import akka.actor.{Actor, ActorLogging}
import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.actors.Indexer.Index
import com.youleligou.crawler.models.ParseResult
import com.youleligou.crawler.services.IndexService

/**
  * Created by dell on 2016/8/29.
  * 索引任务
  */
class Indexer @Inject()(config: Config, indexService: IndexService) extends Actor with ActorLogging {

  override def receive: Receive = {
    case Index(parseResult) =>
      indexService.index(parseResult)
  }
}

object Indexer extends NamedActor {
  override final val Name     = "IndexActor"
  override final val PoolName = "IndexActorPool"

  sealed trait Command
  sealed trait Event

  case class Index(parseResult: ParseResult)
}
