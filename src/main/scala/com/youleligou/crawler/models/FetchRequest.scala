package com.youleligou.crawler.models

/**
  * Created by liangliao on 10/4/17.
  */
case class FetchRequest(requestName: String, urlInfo: UrlInfo, retry: Int = 0)
