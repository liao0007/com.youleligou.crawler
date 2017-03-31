package com.youleligou.crawler.spider.indexer.support

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.entity.HttpPage
import com.youleligou.crawler.spider.indexer.Indexer
import com.youleligou.models.HttpPage

/**
  * Created by dell on 2016/8/29.
  * ES索引器
  */
private[crawler] class ElasticIndexer extends Indexer with LazyLogging {
  /**
    * 索引网页信息
    *
    * @param htmlpage
    * @return
    */
  override def index(htmlpage: HttpPage): Unit = {

  }
}
