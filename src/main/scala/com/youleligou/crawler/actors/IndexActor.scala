package com.youleligou.crawler.actors

import javax.inject.Inject

import akka.actor.Actor
import com.typesafe.config.Config
import com.youleligou.crawler.actors.CountActor.IndexCounter
import com.youleligou.crawler.indexers.Indexer
import com.youleligou.crawler.models.ParseResult

/**
  * Created by dell on 2016/8/29.
  * 索引任务
  */
class IndexActor @Inject()(config: Config, indexer: Indexer) extends Actor {
  private val countActor =
    context.system.actorSelection("akka://" + config.getString("crawler.appName") + "/user/" + CountActor.name)
  context.system.actorSelection("")

  override def receive: Receive = {
    case page: ParseResult =>
      indexer.index(page)
      countActor ! IndexCounter(1)
  }
}

object IndexActor extends NamedActor {

  override final val name = "IndexActor"

}
