package com.youleligou.crawler.indexers

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.models.ParseResult

/**
  * Created by dell on 2016/8/29.
  * ES索引器
  */
class ElasticIndexer extends Indexer with LazyLogging {
  /**
    * 文档索引
    *
    * @param page
    * @return
    */
  override def index(page: ParseResult): Unit = {
    logger.info("indexing " + page.url)
  }
}