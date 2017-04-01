package com.youleligou.crawler.indexers

import java.net.InetAddress

import com.youleligou.crawler.indexers.IndexerConstants
import com.youleligou.crawler.utils.{JsonUtil, MD5Util}
import com.youleligou.crawler.model.PageIndexEntity
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress

/**
 * Created by young.yang on 2016/8/30.
 */
object Elastic4sExample {

  val client = TransportClient.builder().build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("115.29.47.216"), 9300))

  def main(args: Array[String]) {
    val page = new PageIndexEntity
    page.setAuthor("杨勇")
    page.setContent("中华人民共和過")
    page.setTitle("测试")
    page.setUrl("http://www.baidu.com/1")
    page.setPublishTime(System.currentTimeMillis())
    page.setUpdateTime(System.currentTimeMillis())
    client.prepareIndex(IndexerConstants.indexName,IndexerConstants.indexType).setId(MD5Util.md5(page.getUrl)).setSource(JsonUtil.toJson(page)).get()
  }
}
