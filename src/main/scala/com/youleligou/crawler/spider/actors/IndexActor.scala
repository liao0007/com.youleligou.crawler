package com.youleligou.crawler.spider.actors

import javax.inject.Inject

import akka.actor.{Actor, Props}
import com.typesafe.config.Config
import com.youleligou.crawler.entity.HttpPage
import com.youleligou.crawler.spider.indexer.Indexer
import com.youleligou.models.{HttpPage, IndexCounter}

/**
  * Created by dell on 2016/8/29.
  * 索引任务
  */
class IndexActor @Inject()(config: Config)(indexer: Indexer) extends Actor {
  private val countActor =
    context.system.actorSelection("akka://" + config.getString("crawler.appName") + "/user/" + config.getString("crawler.counter.name"))
  context.system.actorSelection("")

  override def receive: Receive = {
    case page: HttpPage =>
      indexer.index(page)
      countActor ! IndexCounter(1)
  }
}

object IndexActor {
  def props(indexer: Indexer) = Props(classOf[IndexActor], indexer)
}
