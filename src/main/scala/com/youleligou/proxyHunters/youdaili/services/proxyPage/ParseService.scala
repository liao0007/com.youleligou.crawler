package com.youleligou.proxyHunters.youdaili.services.proxyPage

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.Injector
import com.youleligou.crawler.daos.CrawlerProxyServerRepo
import com.youleligou.crawler.models.{FetchRequest, FetchResponse, ParseResult, UrlInfo}
import com.youleligou.crawler.services.hash.Md5HashService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

/**
  * Created by young.yang on 2016/8/31.
  * Jsoup解析器
  */
class ParseService @Inject()(config: Config,
                              md5HashService: Md5HashService,
                             crawlerProxyServerRepo: CrawlerProxyServerRepo,
                             @Named(Injector.PoolName) injectors: ActorRef)
    extends com.youleligou.crawler.services.ParseService  {

  private def getChildLinks(document: Document, fetchResponse: FetchResponse) = {
    document.select(".pagelist li").not(".thisclass").asScala.flatMap { li =>
      li.select("a").asScala.find(_.hasAttr("href")).map { a =>
        fetchResponse.fetchRequest.urlInfo.withPath(a.attr("href")).copy(deep = fetchResponse.fetchRequest.urlInfo.deep + 1)
      }
    }
  }

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val document = Jsoup.parse(fetchResponse.content)
    document.select(".chunlist li p a").asScala.filter(_.hasAttr("href")).foreach { a =>
      try {
        val url = a.attr("href").trim
        injectors ! Injector.Inject(
          FetchRequest(
            urlInfo = UrlInfo(
              domain = fetchResponse.fetchRequest.urlInfo.domain,
              path = url.replace(fetchResponse.fetchRequest.urlInfo.domain, ""),
              jobType = config.getString("crawler.job.youdaili.proxyList.jobType"),
              services = Map(
                "ParseService" -> "com.youleligou.proxyHunters.youdaili.services.proxyList.ParseService"
              )

            )
          )
        )
      } catch {
        case NonFatal(x) =>
          logger.warn(x.getMessage)
      }
    }

    ParseResult(
      childLink = getChildLinks(document, fetchResponse),
      fetchResponse = fetchResponse
    )
  }
}