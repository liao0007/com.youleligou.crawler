package com.youleligou.crawler.service.filter

import com.youleligou.crawler.model.UrlInfo

/**
  * Created by liangliao on 1/4/17.
  */
trait FilterService {
  def filter(urlInfo: UrlInfo): Boolean
}
