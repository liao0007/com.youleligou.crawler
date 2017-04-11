package com.youleligou.proxyHunters.xicidaili.services

import java.sql.Timestamp

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
    val originalPage                                  = """[0-9]+""".r.findFirstIn(host).map(_.toInt).getOrElse(1)
    Seq(UrlInfo(host = s"http://www.xicidaili.com/nt/${originalPage + 1}", deep = deep + 1))
  }

  private def persist(proxyServers: Seq[CrawlerProxyServer]) = crawlerProxyServerRepo.create(proxyServers.toList)

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {

    val proxyServers: Seq[CrawlerProxyServer] = Try {
      Jsoup.parse(fetchResponse.content).select("#ip_list tbody tr").asScala.toSeq.drop(1).map { tr =>
        val tds                                                                             = tr.select("td").asScala
        val Seq(_, ip, port, location, isAnonymous, supportedType, _, _, _, lastVerifiedAt) = tds.map(_.text())
        CrawlerProxyServer(
          hash = md5HashService.hash(s"""$ip:$port"""),
          ip = ip,
          port = port.toInt,
          isAnonymous = Some(isAnonymous contains "匿名"),
          supportedType = Some(supportedType),
          location = Some(location),
          reactTime = """[1-9]+""".r.findFirstIn(tds(6).select(".bar").attr("title")).map(_.toFloat),
          lastVerifiedAt = Some(new Timestamp(ProxyListParseService.format.parse(lastVerifiedAt).getTime))
        )
      }
    } getOrElse Seq.empty[CrawlerProxyServer]

    persist(proxyServers)

    ParseResult(
      childLink = if (proxyServers.nonEmpty) getChildLinks(fetchResponse) else Seq.empty[UrlInfo],
      fetchResponse = fetchResponse
    )
  }
}

object ProxyListParseService {
  val format     = new java.text.SimpleDateFormat("yy-MM-dd hh:mm")
  final val name = "ProxyListParseService"
}
