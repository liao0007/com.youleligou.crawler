package com.youleligou.crawler.parsers

import com.youleligou.crawler.entity.UrlInfo
import com.youleligou.crawler.model.{SeedType, UrlInfo}
import com.youleligou.crawler.service.fetch.HttpClientFetchService
import com.youleligou.crawler.service.parse.JsoupParseService

/**
 * Created by dell on 2016/9/1.
 */
object JsoupExample {

  def parserHtml(url:UrlInfo): Unit ={
    val fetcher = new HttpClientFetchService
    val parser = new JsoupParseService
    val page = fetcher.fetch(url)
    println(page)
    val page1 = fetcher.fetch(url)
    println(page1)
    val result = parser.parse(page.get)
    println(result.keywords)
    println(result.desc)
    result.childLink._1.foreach(println _)
  }

  def main(args: Array[String]) {
    val url = "http://bj.fang.com/"
    JsoupExample.parserHtml(UrlInfo(url,"",SeedType,0))

  }
}
