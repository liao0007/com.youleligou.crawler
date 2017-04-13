package com.youleligou.proxyHunters.youdaili.services

import com.google.inject.Inject
import com.youleligou.crawler.daos.{CrawlerProxyServer, CrawlerProxyServerRepo}
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.crawler.services.ParseService
import com.youleligou.crawler.services.hash.Md5HashService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.collection.JavaConverters._
import scala.util.Try
import scala.util.control.NonFatal

/**
  * Created by young.yang on 2016/8/31.
  * Jsoup解析器
  */
class ProxyListParseService @Inject()(md5HashService: Md5HashService, crawlerProxyServerRepo: CrawlerProxyServerRepo) extends ParseService {

  private def getChildLinks(content: Document, fetchResponse: FetchResponse) = {
    content.select(".pagebreak li").not(".thisclass").asScala.flatMap { li =>
      li.select("a").asScala.find(_.hasAttr("href")).map { a =>
        fetchResponse.fetchRequest.urlInfo.withPath(a.attr("href")).copy(deep = fetchResponse.fetchRequest.urlInfo.deep + 1)
      }
    }
  }

  private def persist(proxyServers: Seq[CrawlerProxyServer]) = crawlerProxyServerRepo.create(proxyServers.toList)

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val document = Jsoup.parse(fetchResponse.content)
    val proxyServers: Seq[CrawlerProxyServer] = document
      .select(".content")
      .text()
      .split("#")
      .toSeq flatMap { urlsString =>
      val pattern = """.*\D([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3})\:?([0-9]{1,5})@(HTTP|HTTPS).*""".r
      try {
        val pattern(ip, port, supportedType) = urlsString.trim
        Some(
          CrawlerProxyServer(
            hash = md5HashService.hash(s"""$ip:$port"""),
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

object ProxyListParseService {
  val format     = new java.text.SimpleDateFormat("yy-MM-dd hh:mm")
  final val name = "YouDaiLiProxyListParseService"
}
