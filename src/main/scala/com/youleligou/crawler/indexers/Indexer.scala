package com.youleligou.crawler.indexers

import com.youleligou.crawler.models.ParseResult

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
  def index(page: ParseResult): Unit
}

object Indexer {

  class IndexException(message: String, e: Throwable) extends Exception(message, e) {
    def this(message: String) = this(message, new Exception(message))
  }

}
