package com.youleligou.crawler.service.index

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.model.ParseResult

/**
  * Created by dell on 2016/8/29.
  * ES索引器
  */
class ElasticIndexService extends IndexService with LazyLogging {

  /**
    * 文档索引
    *
    * @param parseResult
    * @return
    */
  override def index(parseResult: ParseResult): Unit = {
    logger.debug("IndexService - indexing " + parseResult.url)
  }
}
