package com.youleligou.crawler.service.index

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.model.ParseResult
import com.youleligou.crawler.service.IndexService

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
    logger.info("indexing: " + parseResult.urlInfo)
  }
}
