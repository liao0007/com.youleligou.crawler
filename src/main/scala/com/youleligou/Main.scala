package com.youleligou

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import com.datastax.driver.core.{ResultSet, Session}
import com.google.inject.name.Named
import com.google.inject.{Guice, Inject}
import com.outworkers.phantom.connectors.KeySpace
import com.outworkers.phantom.dsl.{DatabaseProvider, DateTime}
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.Main.injector
import com.youleligou.crawler.actors.ProxyAssistant
import com.youleligou.crawler.actors.ProxyAssistant.Run
import com.youleligou.crawler.daos.cassandra.crawler.CrawlerJob.JobType
import com.youleligou.crawler.daos.cassandra.crawler.{CrawlerDatabase, CrawlerJob}
import com.youleligou.crawler.daos.mysql
import com.youleligou.crawler.daos.mysql.CrawlerJobRepo
import com.youleligou.crawler.modules.{ActorModule, AkkaModule, ConfigModule, ServiceModule}
import com.youleligou.eleme.{ElemeCrawlerBootstrap, ElemeModule}
import com.youleligou.proxyHunters.xicidaili.{XiCiDaiLiCrawlerBootstrap, XiCiDaiLiModule}
import com.youleligou.proxyHunters.youdaili.{YouDaiLiCrawlerBootstrap, YouDaiLiModule}

import scala.collection.immutable
import scala.concurrent.duration._

/**
  * Created by liangliao on 31/3/17.
  */
class ProxyAssistantBootstrap @Inject()(config: Config, system: ActorSystem, @Named(ProxyAssistant.Name) proxyAssistantActor: ActorRef)
    extends LazyLogging {
  import system.dispatcher
  def start(): Unit = {
    system.scheduler.schedule(0.second, FiniteDuration(config.getInt("crawler.proxy-assistant.interval"), MILLISECONDS), proxyAssistantActor, Run)
  }
}

class CassandraBootstrap @Inject()(config: Config, system: ActorSystem, val database: CrawlerDatabase, crawlerJobRepo: CrawlerJobRepo)
    extends DatabaseProvider[CrawlerDatabase] {
  import system.dispatcher
  def start(): Unit = {

    for {
      _           <- database.createAsync()
      crawlerJobs <- crawlerJobRepo.all()

    } yield {
      crawlerJobs foreach { crawlerJob =>
        database.crawlerJobs
          .store(CrawlerJob(
            jobName = crawlerJob.jobName,
            url = crawlerJob.url,
            useProxy = crawlerJob.useProxy,
            createdAt = new DateTime(crawlerJob.createdAt),
            completedAt = new DateTime(crawlerJob.createdAt),
            statusCode = crawlerJob.statusCode,
            statusMessage = crawlerJob.statusMessage
          ))
          .future()
      }

    }
  }

}

object Main extends App {
  val injector = Guice.createInjector(
    new ConfigModule,
    new AkkaModule,
    new ServiceModule,
    new ActorModule,
    new ElemeModule,
    new XiCiDaiLiModule,
    new YouDaiLiModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._

  injector.instance[CassandraBootstrap].start()

  if (args.contains("eleme/restaurant"))
    injector.instance[ElemeCrawlerBootstrap].startRestaurant()

  if (args.contains("eleme/food"))
    injector.instance[ElemeCrawlerBootstrap].startFood()

  if (args.contains("proxy/assistant"))
    injector.instance[ProxyAssistantBootstrap].start()

  if (args.contains("proxy/you"))
    injector.instance[YouDaiLiCrawlerBootstrap].start()

  if (args.contains("proxy/xici"))
    injector.instance[XiCiDaiLiCrawlerBootstrap].start()
}

//object Main2 extends App {
//  val config = ConfigFactory.load()
//  val system = ActorSystem(config.getString("appName"), config)
//}
