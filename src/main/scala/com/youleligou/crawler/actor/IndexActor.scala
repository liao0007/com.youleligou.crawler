package com.youleligou.crawler.actor

import akka.actor.Actor
import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.crawler.actor.CountActor.IndexCounter
import com.youleligou.crawler.model.ParseResult
import com.youleligou.crawler.service.index.IndexService

/**
  * Created by dell on 2016/8/29.
  * 索引任务
  */
class IndexActor @Inject()(config: Config, indexService: IndexService) extends Actor {
  private val countActor =
    context.system.actorSelection("akka://" + config.getString("crawler.appName") + "/user/" + CountActor.name)
  context.system.actorSelection("")

  override def receive: Receive = {
    case page: ParseResult =>
      indexService.index(page)
      countActor ! IndexCounter(1)
  }
}

object IndexActor extends NamedActor {

  override final val name = "IndexActor"

}
