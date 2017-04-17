package com.youleligou.proxyHunters.xicidaili.services.proxyList

import java.sql.Timestamp

import com.google.inject.Inject
import com.youleligou.crawler.daos.{CrawlerProxyServer, CrawlerProxyServerRepo}
import com.youleligou.crawler.models.{FetchResponse, ParseResult, UrlInfo}
import com.youleligou.crawler.services.hash.Md5HashService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

/**
  * Created by young.yang on 2016/8/31.
  * Jsoup解析器
  */
class ParseService @Inject()(md5HashService: Md5HashService, crawlerProxyServerRepo: CrawlerProxyServerRepo) extends com.youleligou.crawler.services.ParseService {

  val format     = new java.text.SimpleDateFormat("yy-MM-dd hh:mm")

  private def getChildLinks(document: Document, fetchResponse: FetchResponse) = {
    document.select(".pagination a").asScala.filter(_.hasAttr("href")).map { a =>
      fetchResponse.fetchRequest.urlInfo.withPath(a.attr("href")).copy(deep = fetchResponse.fetchRequest.urlInfo.deep + 1)
    }
  }

  private def persist(proxyServers: Seq[CrawlerProxyServer]) = crawlerProxyServerRepo.create(proxyServers.toList)

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val parsedContent: Document = Jsoup.parse(fetchResponse.content)

    val proxyServers: Seq[CrawlerProxyServer] =
      parsedContent.select("#ip_list tbody tr").asScala.drop(1).flatMap { tr =>
        val tds = tr.select("td").asScala
        try {
          val Seq(_, ip, port, location, isAnonymous, supportedType, _, _, _, lastVerifiedAt) = tds.map(_.text())
          Some(
            CrawlerProxyServer(
              hash = md5HashService.hash(s"""$ip:$port"""),
              ip = ip,
              port = port.toInt,
              isAnonymous = Some(isAnonymous contains "高匿"),
              supportedType = Some(supportedType),
              location = Some(location),
              reactTime = """[1-9]+""".r.findFirstIn(tds(6).select(".bar").attr("title")).map(_.toFloat),
              lastVerifiedAt = Some(new Timestamp(format.parse(lastVerifiedAt).getTime))
            ))
        } catch {
          case NonFatal(x) =>
            logger.warn(x.getMessage)
            None
        }
      }

    persist(proxyServers)

    ParseResult(
      childLink = if (proxyServers.nonEmpty) getChildLinks(parsedContent, fetchResponse) else Seq.empty[UrlInfo],
      fetchResponse = fetchResponse
    )
  }
}
