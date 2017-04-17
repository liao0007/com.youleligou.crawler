package com.youleligou.proxyHunters.youdaili

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.Injector
import com.youleligou.crawler.actors.Injector.{ClearCache, Tick}
import com.youleligou.crawler.models.{FetchRequest, UrlInfo}

import scala.concurrent.duration._

class YouDaiLiCrawlerBootstrap @Inject()(config: Config, system: ActorSystem, @Named(Injector.PoolName) injectors: ActorRef) extends LazyLogging {
  import system.dispatcher

  val proxyPageConfig = config.getConfig("crawler.job.youdaili.proxyPage")

  def start(): Unit = {
    import com.github.andr83.scalaconfig._

    implicit val timeout = Timeout(5.minutes)

    for {
      _ <- injectors ? ClearCache(proxyPageConfig.getString("jobType"))
      _ <- injectors ? ClearCache(config.getString("crawler.job.youdaili.proxyList.jobType"))
    } yield {
      val seeds = proxyPageConfig.as[Seq[UrlInfo]]("seed")
      seeds.foreach { seed =>
        injectors ! Injector.Inject(FetchRequest(
                                       urlInfo = seed
                                     ),
                                     force = true)
      }

      system.scheduler.schedule(0.seconds,
                                FiniteDuration(proxyPageConfig.getInt("interval"), MILLISECONDS),
                                injectors,
                                Tick(proxyPageConfig.getString("jobType")))
      system.scheduler.schedule(
        30.seconds,
        FiniteDuration(proxyPageConfig.getInt("proxyList.interval"), MILLISECONDS),
        injectors,
        Tick(config.getString("crawler.job.youdaili.proxyList.jobType"))
      )
    }
  }

}
