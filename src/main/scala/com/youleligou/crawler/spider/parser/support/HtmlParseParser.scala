package com.youleligou.crawler.spider.parser.support

import com.youleligou.crawler.entity.HttpPage
import com.youleligou.crawler.spider.parser.Parser
import com.youleligou.models.{HttpPage, HttpResult}

/**
  * Created by young.yang on 2016/8/28.
  */
private[crawler] class HtmlParseParser extends Parser {
  override def parse(html: HttpResult): HttpPage = {
    val page = new HttpPage
    page.setContent(html.content)
    page.setUrl(html.url)
    page
  }
}
