package com.youleligou.proxyHunters.youdaili.services

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.youleligou.crawler.actors.AbstractInjectActor
import com.youleligou.crawler.actors.AbstractInjectActor.Tick
import com.youleligou.crawler.daos.CrawlerProxyServerRepo
import com.youleligou.crawler.models.{FetchRequest, FetchResponse, ParseResult, UrlInfo}
import com.youleligou.crawler.services.ParseService
import com.youleligou.crawler.services.hash.Md5HashService
import com.youleligou.proxyHunters.youdaili.ProxyListInjectActor
import org.jsoup.Jsoup

import scala.collection.JavaConverters._
import scala.util.Try

/**
  * Created by young.yang on 2016/8/31.
  * Jsoup解析器
  */
class ProxyPageParseService @Inject()(md5HashService: Md5HashService,
                                      crawlerProxyServerRepo: CrawlerProxyServerRepo,
                                      @Named(ProxyListInjectActor.poolName) injectorPool: ActorRef)
    extends ParseService {

  private def getChildLinks(fetchResponse: FetchResponse) = {
    val UrlInfo(host, queryParameters, urlType, deep) = fetchResponse.fetchRequest.urlInfo
    val urls = Jsoup.parse(fetchResponse.content).select(".pagelist li").not(".thisclass").asScala.toSeq.flatMap { li =>
      li.select("a").asScala.toSeq.headOption.map { a =>
        UrlInfo(host = "http://www.youdaili.net/Daili/guonei/" + a.attr("href"), deep = deep + 1)
      }
    }
    if (urls.isEmpty) logger.warn("no child links for {}", fetchResponse.fetchRequest.urlInfo)
    urls
  }

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    Jsoup.parse(fetchResponse.content).select(".chunlist li p a").asScala.toSeq.foreach { a =>
      Try {
        val href = a.attr("href")
        injectorPool ! AbstractInjectActor.Inject(
          FetchRequest(
            requestName = "fetch_youdaili_proxy_page",
            urlInfo = UrlInfo(host = href)
          )
        )
      } getOrElse Unit
    }

    ParseResult(
      childLink = getChildLinks(fetchResponse),
      fetchResponse = fetchResponse
    )
  }
}

object ProxyPageParseService {
  val format     = new java.text.SimpleDateFormat("yy-MM-dd hh:mm")
  final val name = "YouDaiLiProxyPageParseService"
}
