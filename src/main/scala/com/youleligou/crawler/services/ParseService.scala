package com.youleligou.crawler.services

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.models.{FetchResponse, ParseResult}

/**
  * Created by young.yang on 2016/8/28.
  * html页面解析接口
  */
trait ParseService extends LazyLogging {
  def parse(fetchResponse: FetchResponse): ParseResult
}
