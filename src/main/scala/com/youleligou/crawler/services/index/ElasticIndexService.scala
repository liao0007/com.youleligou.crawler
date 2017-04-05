package com.youleligou.crawler.services.index

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.models.ParseResult
import com.youleligou.crawler.services.IndexService

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
