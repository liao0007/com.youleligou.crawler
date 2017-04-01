package com.youleligou.crawler.boot

import com.google.inject.Inject

import akka.actor._
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actor.CountActor._
import com.youleligou.crawler.actor._
import com.youleligou.crawler.model.UrlInfo.SeedType
import com.youleligou.crawler.model.UrlInfo
import com.youleligou.crawler.module.GuiceAkkaExtension

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, _}

/**
  * Created by dell on 2016/8/29.
  * 爬虫主函数
  */
class CrawlerBoot @Inject()(config: Config, system: ActorSystem) extends LazyLogging {
  implicit val timeout = Duration(5, "s")
  lazy val countActor: ActorSelection =
    system.actorSelection("akka://" + config.getString("crawler.appName") + "/user/" + config.getString("crawler.count.name"))

  /**
    * 爬虫启动函数
    */
  def start(): Unit = {
    val indexActor = system.actorOf(
      RoundRobinPool(config.getInt("crawler.actor.index.parallel")).props(GuiceAkkaExtension(system).props(IndexActor.name)),
      IndexActor.name
    )
    logger.info("created indexActor name -[" + indexActor + "]")

    val parseActor = system.actorOf(
      RoundRobinPool(config.getInt("crawler.actor.parse.parallel")).props(GuiceAkkaExtension(system).props(ParseActor.name)),
      ParseActor.name
    )
    logger.info("create parseActor name -[" + parseActor + "]")

    val fetchActor = system.actorOf(
      RoundRobinPool(config.getInt("crawler.actor.fetch.parallel")).props(GuiceAkkaExtension(system).props(FetchActor.name)),
      FetchActor.name
    )
    logger.info("create fetchActor name -[" + fetchActor + "]")

    val injectActor = system.actorOf(
      RoundRobinPool(config.getInt("crawler.actor.inject.parallel")).props(GuiceAkkaExtension(system).props(InjectActor.name)),
      InjectActor.name
    )
    logger.info("create injectActor name -[" + injectActor + "]")

    system.actorOf(GuiceAkkaExtension(system).props(CountActor.name), CountActor.name)
    logger.info("create countActor name -[" + countActor + "]")

    config.getStringList("crawler.seed").asScala.toList.foreach(url => injectActor ! UrlInfo(url.trim, url.trim, SeedType, 0))
  }

  /**
    * 停止爬虫程序
    */
  def stop(): Unit = {
    system.terminate()
  }

  implicit val tm = Timeout(100, SECONDS)

  def printCount(): String = {
    val result = countActor ? PrintCounter
    Await.result(result, timeout).asInstanceOf[String]
  }

  def getCounter: AllCounter = {
    val result = countActor ? GetAllCounter
    Await.result(result, timeout).asInstanceOf[AllCounter]
  }
}
