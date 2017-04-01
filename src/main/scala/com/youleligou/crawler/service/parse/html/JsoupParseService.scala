package com.youleligou.crawler.service.parse.html

import com.youleligou.crawler.model.UrlInfo.GenerateType
import com.youleligou.crawler.model.{FetchResult, ParseResult, UrlInfo}
import com.youleligou.crawler.service.parse.ParseService
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
  private def parserUrls(urlInfo: UrlInfo, urls: Elements): List[UrlInfo] = {
    urls.asScala.toList
      .map(_.attr("href"))
      .flatMap {
        case url if url.startsWith("http") => Some(UrlInfo(url, urlInfo.queryParameters, GenerateType, urlInfo.deep + 1))
        case url if url.startsWith("//") => None
        case url if url.startsWith("/") => Some(UrlInfo(urlInfo.domain + url, urlInfo.queryParameters, GenerateType, urlInfo.deep + 1))
        case _ => None
      }
  }

  /**
    * 解析具体实现
    */
  override def parse(fetchResult: FetchResult): ParseResult = {
    val document = Jsoup.parse(fetchResult.content)
    ParseResult(
      urlInfo = fetchResult.urlInfo,
      title = Some(document.title()),
      content = document.text(),
      publishTime = System.currentTimeMillis(),
      updateTime = System.currentTimeMillis(),
      childLink = parserUrls(fetchResult.urlInfo, document.body().select("a"))
    )
  }
}
