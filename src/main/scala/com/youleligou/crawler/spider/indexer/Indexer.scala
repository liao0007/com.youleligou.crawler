package com.youleligou.crawler.spider.indexer

import com.youleligou.crawler.config.{CrawlerConfig, CrawlerConfigContants}
import com.youleligou.crawler.entity.HttpPage
import com.youleligou.models.HttpPage

/**
  * Created by dell on 2016/8/29.
  * 索引接口
  */
trait Indexer {

  /**
    * 文档索引
    *
    * @param page
    * @return
    */
  def index(page: HttpPage): Unit
}

object Indexer {
  class IndexException(message: String, e: Throwable) extends Exception(message, e) {
    def this(message: String) = this(message, new Exception(message))
  }
}