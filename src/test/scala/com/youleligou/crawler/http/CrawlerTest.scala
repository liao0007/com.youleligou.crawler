package com.youleligou.crawler.http

import com.youleligou.crawler.entity.UrlInfo
import com.youleligou.crawler.fetchers.support.HttpWatch
import com.youleligou.crawler.models.{SeedType, UrlInfo}

/**
 * Created by young.yang on 2016/8/28.
 */
object CrawlerTest {

  def main(args: Array[String]) {
    val url = "http://www.sina.com.cn"
    val result = HttpWatch.get(UrlInfo(url,"",SeedType,0))
    println(result.content)
    println(result.status)
  }
}
