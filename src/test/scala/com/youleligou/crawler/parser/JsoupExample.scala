package com.youleligou.crawler.parser

import com.youleligou.crawler.entity.UrlInfo
import com.youleligou.crawler.spider.fetcher.HttpClientFetcher
import com.youleligou.crawler.spider.parser.JsoupParser
import com.youleligou.models.{SeedType, UrlInfo}

/**
 * Created by dell on 2016/9/1.
 */
object JsoupExample {

  def parserHtml(url:UrlInfo): Unit ={
    val fetcher = new HttpClientFetcher
    val parser = new JsoupParser
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
