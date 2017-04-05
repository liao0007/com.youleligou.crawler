package com.youleligou.eleme

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractInjectActor.Init
import com.youleligou.eleme.actors.RestaurantInjectActor

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._

/**
  * Created by dell on 2016/8/29.
  * 爬虫主函数
  */
class ElemeCrawlerBootstrap @Inject()(config: Config, system: ActorSystem, @Named(RestaurantInjectActor.poolName) injectActor: ActorRef) extends LazyLogging {

  /**
    * 爬虫启动函数
    */
  def start(): Unit = {
    system.scheduler.scheduleOnce(FiniteDuration(300, MILLISECONDS), injectActor, Init)

  }

  /**
    * 停止爬虫程序
    */
  def stop(): Unit = {
    system.terminate()
  }
}
