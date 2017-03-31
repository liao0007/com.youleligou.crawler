package com.youleligou.crawler.spider.parser.support

import com.youleligou.crawler.entity.{GenerateType, HttpPage, UrlInfo}
import com.youleligou.crawler.spider.parser.Parser
import com.youleligou.models.{GenerateType, HttpPage, HttpResult, UrlInfo}

import scala.collection.mutable.ListBuffer

/**
  * Created by young.yang on 2016/8/31.
  * Jsoup解析器
  */
private[crawler] class JsoupParser extends Parser {

  private val KEYWORDS = "keywords"

  private val DESCRIPTION = "description"

  /**
    * 解析meta信息
    *
    * @param key
    * @param meta
    * @return
    */
  private def getMeta(key: String, meta: Elements): String = {
    for (i <- 0 until meta.size()) {
      val element = meta.get(i)
      if (key.equals(element.attr("name").toLowerCase)) {
        return element.attr("content")
      }
    }
    ""
  }

  /**
    * 解析子url
    */
  private def parserUrls(urls: Elements, deep: Int): (List[UrlInfo], Int) = {
    val list = new ListBuffer[UrlInfo]()
    for (i <- 0 until urls.size()) {
      val element = urls.get(i)
      val url = element.attr("href")
      if (url.startsWith("http"))
        list.append(UrlInfo(url, "", GenerateType, deep + 1))
    }
    (list.toList, deep + 1)
  }

  /**
    * 解析具体实现
    *
    * @param html
    * @return
    */
  override def parse(html: HttpResult): HttpPage = {
    val htmlPage = new HttpPage
    val document = Jsoup.parse(html.content)
    val meta = document.select("meta")
    htmlPage.setTitle(document.title())
    htmlPage.setContent(document.text())
    // htmlPage.setHtml(html.content)
    htmlPage.setPublishTime(System.currentTimeMillis())
    htmlPage.setUpdateTime(System.currentTimeMillis())
    htmlPage.setUrl(html.url)
    htmlPage.setKeywords(getMeta(KEYWORDS, meta))
    htmlPage.setDesc(getMeta(DESCRIPTION, meta))
    htmlPage.setChildLink(parserUrls(document.body().select("a"), html.deep))
    htmlPage
  }
}
