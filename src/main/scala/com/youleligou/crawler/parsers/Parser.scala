package com.youleligou.crawler.parsers

import com.youleligou.crawler.models.{ParseResult, FetchResult}

/**
  * Created by young.yang on 2016/8/28.
  * html页面解析接口
  */
trait Parser {
  def parse(fetchResult: FetchResult): ParseResult
}

object Parser {
  class ParseException(message: String, e: Throwable) extends Exception(message, e) {
    def this(message: String) = this(message, new Exception(message))
  }
}
