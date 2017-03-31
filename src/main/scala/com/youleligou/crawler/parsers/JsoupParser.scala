package com.youleligou.crawler.parsers

import com.youleligou.crawler.models.UrlInfo.GenerateType
import com.youleligou.crawler.models.{FetchResult, ParseResult, UrlInfo}
import org.jsoup.Jsoup
import org.jsoup.select.Elements

import scala.collection.JavaConverters._

/**
  * Created by young.yang on 2016/8/31.
  * Jsoup解析器
  */
class JsoupParser extends Parser {

  /**
    * 解析子url
    */
  private def parserUrls(parentUrl: String, urls: Elements, deep: Int): List[UrlInfo] = {
    urls.asScala.toList.map(_.attr("href")).withFilter(_.startsWith("http")).map { url =>
      UrlInfo(url, parentUrl, GenerateType, deep + 1)
    }
  }

  /**
    * 解析具体实现
    */
  override def parse(fetchResult: FetchResult): ParseResult = {
    val document = Jsoup.parse(fetchResult.content)
    ParseResult(
      url = fetchResult.url,
      title = document.title(),
      content = document.text(),
      publishTime = System.currentTimeMillis(),
      updateTime = System.currentTimeMillis(),
      childLink = parserUrls(fetchResult.url, document.body().select("a"), fetchResult.deep)
    )
  }
}
