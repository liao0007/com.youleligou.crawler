package com.youleligou.proxyHunters.youdaili.services.proxyList

import com.google.inject.Inject
import com.outworkers.phantom.database.DatabaseProvider
import com.youleligou.crawler.daos.ProxyServerDao
import com.youleligou.crawler.daos.cassandra.CrawlerDatabase
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

/**
  * Created by young.yang on 2016/8/31.
  * Jsoup解析器
  */
class ParseService @Inject()(val database: CrawlerDatabase)
    extends com.youleligou.crawler.services.ParseService
    with DatabaseProvider[CrawlerDatabase] {

  private def getChildLinks(content: Document, fetchResponse: FetchResponse) = {
    content.select(".pagebreak li").not(".thisclass").asScala.flatMap { li =>
      li.select("a").asScala.find(_.hasAttr("href")).map { a =>
        fetchResponse.fetchRequest.urlInfo.withPath(a.attr("href")).copy(deep = fetchResponse.fetchRequest.urlInfo.deep + 1)
      }
    }
  }

  private def persist(proxyServers: Seq[ProxyServerDao]) = database.crawlerProxyServers.batchInsertOrUpdate(proxyServers)

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val document = Jsoup.parse(fetchResponse.content)
    val proxyServers: Seq[ProxyServerDao] = document
      .select(".content")
      .text()
      .split("#")
      .toSeq
      .map(_.replaceAll("[^\\x00-\\x7F]", "").trim)
      .filter(_.length > 0) flatMap { urlsString =>
      val pattern = """.*?([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3})\:?([0-9]{1,5})@(HTTP|HTTPS).*""".r
      try {
        val pattern(ip, port, supportedType) = urlsString
        Some(
          ProxyServerDao(
            ip = ip,
            port = port.toInt,
            supportedType = Some(supportedType)
          ))
      } catch {
        case NonFatal(x) =>
          logger.warn(x.getMessage)
          None
      }
    }

    persist(proxyServers)

    ParseResult(
      childLink = if (proxyServers.nonEmpty) getChildLinks(document, fetchResponse) else Seq.empty[UrlInfo],
      fetchResponse = fetchResponse
    )
  }
}
