package com.youleligou.crawler.services

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractFetchActor.FetchResult
import com.youleligou.crawler.actors.AbstractParseActor.ParseResult

/**
  * Created by young.yang on 2016/8/28.
  * html页面解析接口
  */
trait ParseService extends LazyLogging {
  def parse(fetchResult: FetchResult): ParseResult
}

object ParseService {

}
