package com.youleligou.eleme

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractFetchActor.Fetch
import com.youleligou.crawler.actors.AbstractInjectActor
import com.youleligou.crawler.daos.CrawlerJob.FetchJobType
import com.youleligou.crawler.daos.CrawlerJobRepo
import com.youleligou.crawler.models.{FetchRequest, UrlInfo}
import com.youleligou.crawler.models.UrlInfo.UrlType
import com.youleligou.eleme.actors.RestaurantInjectActor
import com.youleligou.eleme.services.RestaurantInjectService
import com.youleligou.proxyHunters.xicidaili.services.ProxyListInjectService

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by dell on 2016/8/29.
  * 爬虫主函数
  */
class ElemeCrawlerBootstrap @Inject()(config: Config, @Named(RestaurantInjectActor.poolName) injectActor: ActorRef) extends LazyLogging {

  /**
    * 爬虫启动函数
    */
  def start(): Unit = {
    injectActor ! AbstractInjectActor.Inject(
      FetchRequest(
        requestName = "fetch_eleme_restaurant",
        urlInfo = UrlInfo(
          host = "http://mainsite-restapi.ele.me/shopping/restaurants",
          queryParameters = Map(
            "latitude"  -> "39.88529",
            "longitude" -> "116.45744",
            "offset"    -> "0"
          ),
          urlType = UrlType.Seed
        )
      ))

  }

}
