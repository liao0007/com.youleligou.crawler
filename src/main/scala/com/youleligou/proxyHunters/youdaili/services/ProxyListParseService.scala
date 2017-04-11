package com.youleligou.proxyHunters.youdaili.services

import com.google.inject.Inject
import com.youleligou.crawler.daos.{CrawlerProxyServer, CrawlerProxyServerRepo}
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.crawler.services.ParseService
import com.youleligou.crawler.services.hash.Md5HashService
import org.jsoup.Jsoup

import scala.collection.JavaConverters._
import scala.util.Try

/**
  * Created by young.yang on 2016/8/31.
  * Jsoup解析器
  */
class ProxyListParseService @Inject()(md5HashService: Md5HashService, crawlerProxyServerRepo: CrawlerProxyServerRepo) extends ParseService {

  private def getChildLinks(fetchResponse: FetchResponse) = {
    val UrlInfo(host, queryParameters, urlType, deep) = fetchResponse.fetchRequest.urlInfo
    Jsoup.parse(fetchResponse.content).select(".pagebreak li").not(".thisclass").asScala.toSeq.flatMap { li =>
      li.select("a").asScala.toSeq.headOption.map { a =>
        UrlInfo(host = "http://www.youdaili.net/Daili/guonei/" + a.attr("href"), deep = deep + 1)
      }
    }
  }

  private def persist(proxyServers: Seq[CrawlerProxyServer]) = crawlerProxyServerRepo.create(proxyServers.toList)

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val content = Jsoup.parse(fetchResponse.content).select(".content").html()

    val proxyServers
      : Seq[CrawlerProxyServer] = Jsoup.parse(content.replaceAll("</p><p>", "~~").replaceAll("<br>", "~~")).text().split("~~").toSeq flatMap {
      urlsString =>
        val pattern = """(.*):([0-9]+)@(.*)#(.*)""".r
        Try {
          val pattern(ip, port, supportedType, location) = urlsString.trim
          Some(
            CrawlerProxyServer(
              hash = md5HashService.hash(s"""$ip:$port"""),
              ip = ip,
              port = port.toInt,
              supportedType = Some(supportedType)
            ))
        } getOrElse None
    }

    persist(proxyServers)

    ParseResult(
      childLink = if (proxyServers.nonEmpty) getChildLinks(fetchResponse) else Seq.empty[UrlInfo],
      fetchResponse = fetchResponse
    )
  }
}

object ProxyListParseService {
  val format     = new java.text.SimpleDateFormat("yy-MM-dd hh:mm")
  final val name = "YouDaiLiProxyListParseService"
}
