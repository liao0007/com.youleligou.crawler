package com.youleligou.crawler.boot

import com.google.inject.Inject
import akka.actor._
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actor._
import com.youleligou.crawler.model.UrlInfo.SeedType
import com.youleligou.crawler.model.UrlInfo

import scala.collection.JavaConverters._

/**
  * Created by dell on 2016/8/29.
  * 爬虫主函数
  */
class CrawlerBoot @Inject()(config: Config, system: ActorSystem, @Named(InjectActor.poolName) injectActor: ActorRef) extends LazyLogging {

  /**
    * 爬虫启动函数
    */
  def start(): Unit = {
    config.getStringList("crawler.seed").asScala.toList.foreach(url => injectActor ! UrlInfo(url.trim, url.trim, SeedType, 0))
  }

  /**
    * 停止爬虫程序
    */
  def stop(): Unit = {
    system.terminate()
  }
}
