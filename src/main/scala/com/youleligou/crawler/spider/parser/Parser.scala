package com.youleligou.crawler.spider.parser

import com.youleligou.crawler.entity.HttpPage
import com.youleligou.models.{HttpPage, HttpResult}

/**
  * Created by young.yang on 2016/8/28.
  * html页面解析接口
  */
trait Parser {
  def parse(html: HttpResult): HttpPage
}
