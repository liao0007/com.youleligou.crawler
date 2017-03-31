package com.youleligou.crawler.boot

import javax.inject.Inject

import akka.actor._
import akka.routing.RoundRobinPool
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.spider.actors._
import akka.pattern.ask
import com.youleligou.crawler.spider.actors.CountActor._
import com.youleligou.crawler.spider.actors.InjectActor.InitSeed
import com.youleligou.crawler.spider.fetcher.HttpClientFetcher
import com.youleligou.crawler.spider.indexer.ElasticIndexer
import com.youleligou.crawler.spider.parser.JsoupParser

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

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
      RoundRobinPool(config.getInt("crawler.index.parallel")).props(IndexActor.props(new ElasticIndexer)),
      config.getString("crawler.index.name")
    )
    logger.info("created indexActor name -[" + indexActor + "]")

    val parseActor = system.actorOf(
      RoundRobinPool(config.getInt("crawler.parse.parallel")).props(ParseActor.props(new JsoupParser, indexActor)),
      config.getString("crawler.parse.name")
    )
    logger.info("create parseActor name -[" + parseActor + "]")

    val fetchActor = system.actorOf(
      RoundRobinPool(config.getInt("crawler.fetch.parallel")).props(FetchActor.props(parseActor)),
      config.getString("crawler.fetch.name")
    )
    logger.info("create fetchActor name -[" + fetchActor + "]")

    val injectActor = system.actorOf(
      RoundRobinPool(config.getInt("crawler.inject.parallel")).props(InjectActor.props(fetchActor)),
      config.getString("crawler.inject.name")
    )
    logger.info("create injectActor name -[" + injectActor + "]")

    system.actorOf(Props[CountActor], config.getString("crawler.counter.name"))
    logger.info("create countActor name -[" + countActor + "]")

    injectActor ! InitSeed(config.getStringList("crawler.seed").asScala.toList)
  }

  /**
    * 停止爬虫程序
    */
  def stop(): Unit = {
    system.terminate()
  }

  def printCount(): String = {
    val result = countActor ? PrintCounter
    Await.result(result, timeout).asInstanceOf[String]
  }

  def getCounter: AllCounter = {
    val result = countActor ? GetAllCounter
    Await.result(result, timeout).asInstanceOf[AllCounter]
  }

  def main(args: Array[String]) {
    //   Thread.sleep(3000)
    //    println(CrawlerBoot.printCount())
    //    println(CrawlerBoot.getCounter())
    // CrawlerBoot.stop()
  }
}
