package com.youleligou.crawler.services

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.models.UrlInfo

/**
  * Created by liangliao on 1/4/17.
  */
trait FilterService extends LazyLogging {
  def filter(urlInfo: UrlInfo): Boolean
}
