package com.youleligou.proxyHunters.youdaili.services

import java.sql.Timestamp

import com.google.inject.Inject
import com.youleligou.crawler.daos.CrawlerProxyServerRepo
import com.youleligou.crawler.models.{FetchResult, ParseResult, UrlInfo}
import com.youleligou.crawler.services.ParseService
import com.youleligou.crawler.services.hash.Md5HashService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.collection.JavaConverters._

/**
  * Created by young.yang on 2016/8/31.
  * Jsoup解析器
  */
class ProxyListParseService @Inject()(md5HashService: Md5HashService, crawlerProxyServerRepo: CrawlerProxyServerRepo) extends ParseService {

  private def persist(document: Document) = {

    val proxyServers = document.select("p").asScala.map { p =>
      val line = p.text()
      """(.*):([0-9]+)@(.*)#(.*)""".r
//      val Seq(_, ip, port, location, isAnonymous, supportedType, _, _, _, lastVerifiedAt) = tds.map(_.text())
//      CrawlerProxyServer(
//        hash = md5HashService.hash(s"""$ip:$port"""),
//        ip = ip,
//        port = port.toInt,
//        isAnonymous = Some(isAnonymous contains "匿名"),
//        supportedType = Some(supportedType),
//        location = Some(location),
//        reactTime = """[1-9]+""".r.findFirstIn(tds(6).select(".bar").attr("title")).map(_.toFloat),
//        lastVerifiedAt = Some(new Timestamp(ProxyListParseService.format.parse(lastVerifiedAt).getTime))
//      )
    }
//    crawlerProxyServerRepo.create(proxyServers.toList)
  }

  /**
    * 解析具体实现
    */
  override def parse(fetchResult: FetchResult): ParseResult = {
    val document: Document = Jsoup.parse(fetchResult.content)
    persist(document)
    ParseResult(
      urlInfo = fetchResult.urlInfo,
      title = Some(document.title()),
      content = document.text(),
      publishTime = System.currentTimeMillis(),
      updateTime = System.currentTimeMillis(),
      childLink = List.empty[UrlInfo]
    )
  }
}

object ProxyListParseService {
  val format = new java.text.SimpleDateFormat("yy-MM-dd hh:mm")
  final val name = "ProxyListParseService"
}
