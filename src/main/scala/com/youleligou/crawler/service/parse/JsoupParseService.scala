package com.youleligou.crawler.service.parse

import com.youleligou.crawler.model.UrlInfo.GenerateType
import com.youleligou.crawler.model.{FetchResult, ParseResult, UrlInfo}
import org.jsoup.Jsoup
import org.jsoup.select.Elements

import scala.collection.JavaConverters._

/**
  * Created by young.yang on 2016/8/31.
  * Jsoup解析器
  */
class JsoupParseService extends ParseService {

  /**
    * 解析子url
    */
  private def parserUrls(domain: String, urls: Elements, deep: Int): List[UrlInfo] = {
    urls.asScala.toList.map(_.attr("href")).map {
      case url if url.startsWith("http") => UrlInfo(url, domain, GenerateType, deep + 1)
      case url if url.startsWith("//") => UrlInfo(url.replace("//", ""), domain, GenerateType, deep + 1)
      case url if url.startsWith("/") => UrlInfo(domain + url, domain, GenerateType, deep + 1)
      case url => UrlInfo(domain + "/" + url, domain, GenerateType, deep + 1)
    }
  }

  /**
    * 解析具体实现
    */
  override def parse(fetchResult: FetchResult): ParseResult = {
    val document = Jsoup.parse(fetchResult.content)
    ParseResult(
      url = fetchResult.url,
      title = Some(document.title()),
      content = document.text(),
      publishTime = System.currentTimeMillis(),
      updateTime = System.currentTimeMillis(),
      childLink = parserUrls(fetchResult.url, document.body().select("a"), fetchResult.deep)
    )
  }
}
