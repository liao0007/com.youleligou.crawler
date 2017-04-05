package com.youleligou.crawler.services

import com.youleligou.crawler.models.UrlInfo

/**
  * Created by liangliao on 1/4/17.
  */
trait FilterService {
  def filter(urlInfo: UrlInfo): Boolean
}
