package com.youleligou.crawler.actors

import javax.inject.Inject

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.typesafe.config.Config
import com.youleligou.crawler.actors.CountActor._
import com.youleligou.crawler.actors.InjectActor.InitSeed
import com.youleligou.crawler.models.UrlInfo.SeedType
import com.youleligou.crawler.models._

/**
  * 抓取种子注入任务,将需要抓取的任务注入到该任务中
  */
class InjectActor @Inject()(config: Config, fetchActor: ActorRef) extends Actor with ActorLogging {
  private val countActor =
    context.system.actorSelection("akka://" + config.getString("crawler.appName") + "/user/" + config.getString("crawler.counter.name"))

  override def receive: Receive = {
    //初始化注入
    case init: InitSeed =>
      val seeds = init.seeds.map(_.trim).map(Seed)
      log.info("init seeds -" + seeds)
      seeds.foreach(f = seed => {
        fetchActor ! UrlInfo(seed.url, null, SeedType, 0)
        countActor ! InjectCounter(1)
      })

    //子url注入
    case urls: List[UrlInfo] =>
      log.info("inject urls -" + urls)
      urls
        .filter(seed => seed.url.startsWith("http"))
        .foreach(seed => {
          fetchActor ! seed
          countActor ! InjectCounter(1)
        })
  }
}

object InjectActor extends NamedActor {

  override final val name = "InjectActor"

  case class InitSeed(seeds: List[String])

}
