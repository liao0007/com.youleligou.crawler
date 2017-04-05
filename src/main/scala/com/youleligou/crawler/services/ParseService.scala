package com.youleligou.crawler.services

import com.youleligou.crawler.models.{FetchResult, ParseResult}

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
