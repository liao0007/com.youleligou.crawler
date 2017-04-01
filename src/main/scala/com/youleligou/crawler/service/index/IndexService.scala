package com.youleligou.crawler.service.index

import com.youleligou.crawler.model.ParseResult

/**
  * Created by dell on 2016/8/29.
  * 索引接口
  */
trait IndexService {

  /**
    * 文档索引
    *
    * @param page
    * @return
    */
  def index(page: ParseResult): Unit
}

object IndexService {

  class IndexException(message: String, e: Throwable) extends Exception(message, e) {
    def this(message: String) = this(message, new Exception(message))
  }

}
