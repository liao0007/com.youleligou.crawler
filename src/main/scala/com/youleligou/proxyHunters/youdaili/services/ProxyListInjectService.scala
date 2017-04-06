package com.youleligou.proxyHunters.youdaili.services

import com.google.inject.Inject
import com.youleligou.crawler.actors.AbstractFetchActor.Fetch
import com.youleligou.crawler.actors.AbstractInjectActor.SeedInitialized
import com.youleligou.crawler.daos.CrawlerJob.FetchJobType
import com.youleligou.crawler.daos.CrawlerJobRepo
import com.youleligou.crawler.models.UrlInfo
import com.youleligou.crawler.models.UrlInfo.GenerateType
import com.youleligou.crawler.services.InjectService

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class ProxyListInjectService @Inject()(crawlerJobRepo: CrawlerJobRepo) extends InjectService {

  override def initSeed(): Future[SeedInitialized] = {
    val idPattern = """[0-9]+""".r
    crawlerJobRepo.findWithMaxId(FetchJobType, ProxyListInjectService.fetchJobName) map {
      case Some(crawlerJob) => idPattern.findFirstIn(crawlerJob.url).map(_.toInt).getOrElse(0)
      case None => 0
    } map (_ % ProxyListInjectService.maxPage) map SeedInitialized
  }

  override def generateFetch(seed: Int): Fetch = {
    Fetch(
      ProxyListInjectService.fetchJobName,
      UrlInfo(
        s"http://www.youdaili.net/Daili/guonei/$seed.html",
        Set.empty[(String, String)],
        GenerateType,
        0
      )
    )
  }
}

object ProxyListInjectService {
  val maxPage = 36718
  final val name = "YouDaiLiInjectService"
  final val fetchJobName = "fetch_youdaili_list"
}
