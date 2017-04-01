package com.youleligou.crawler.service.parse

import com.youleligou.crawler.model.{FetchResult, ParseResult, UrlInfo}
import org.jsoup.select.Elements

/**
  * Created by young.yang on 2016/8/28.
  * html页面解析接口
  */
trait ParseService {
  def parse(fetchResult: FetchResult): ParseResult
}

object ParseService {
  class ParseException(message: String, e: Throwable) extends Exception(message, e) {
    def this(message: String) = this(message, new Exception(message))
  }
}
