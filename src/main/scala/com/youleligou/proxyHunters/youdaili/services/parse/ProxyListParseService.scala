package com.youleligou.proxyHunters.youdaili.services.parse

import com.google.inject.Inject
import com.youleligou.core.reps.Repo
import com.youleligou.crawler.daos.ProxyServerDao
import com.youleligou.crawler.models.{FetchResponse, ParseResult, ProxyServer, UrlInfo}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

/**
  * Created by young.yang on 2016/8/31.
  * Jsoup解析器
  */
class ProxyListParseService @Inject()(proxyServerRepo: Repo[ProxyServerDao])
    extends com.youleligou.crawler.services.ParseService {

  private def getChildLinks(content: Document, fetchResponse: FetchResponse) = {
    content.select(".pagebreak li").not(".thisclass").asScala.flatMap { li =>
      li.select("a").asScala.find(_.hasAttr("href")).map { a =>
        fetchResponse.fetchRequest.urlInfo.withPath(a.attr("href")).copy(deep = fetchResponse.fetchRequest.urlInfo.deep + 1)
      }
    }
  }

  private def persist(proxyServers: Seq[ProxyServerDao]) = proxyServerRepo.save(proxyServers)

  /**
    * 解析具体实现
    */
  override def parse(fetchResponse: FetchResponse): ParseResult = {
    val document = Jsoup.parse(fetchResponse.content)
    val proxyServers: Seq[ProxyServer] = document
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
          ProxyServer(
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
