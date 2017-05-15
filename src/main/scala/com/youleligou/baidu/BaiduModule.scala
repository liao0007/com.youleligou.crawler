package com.youleligou.baidu

import com.google.inject.AbstractModule
import com.youleligou.baidu.daos._
import com.youleligou.baidu.services._
import com.youleligou.core.modules.GuiceAkkaActorRefProvider
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.crawler.services.{FetchService, ParseService}
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 11/5/17.
  */
class BaiduModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  override def configure() {
    /*
      cassandra
     */
    bind[CassandraRepo[ShopDao]].to[repos.cassandra.ShopRepo]
    bind[CassandraRepo[CategoryDao]].to[repos.cassandra.CategoryRepo]

    bind[CassandraRepo[ShopSnapshotDao]].to[repos.cassandra.ShopSnapshotRepo]
    bind[CassandraRepo[CategorySnapshotDao]].to[repos.cassandra.CategorySnapshotRepo]
    bind[CassandraRepo[DishSnapshotDao]].to[repos.cassandra.DishSnapshotRepo]
    bind[CassandraRepo[DishAttributeSnapshotDao]].to[repos.cassandra.DishAttributeSnapshotRepo]

    /*
  es
     */
    bind[ElasticSearchRepo[ShopSnapshotDaoSearch]].to[repos.elasticsearch.ShopSnapshotRepo]
    bind[ElasticSearchRepo[DishSnapshotDaoSearch]].to[repos.elasticsearch.DishSnapshotRepo]

    /*
  services
     */
    bind[ParseService].annotatedWithName(classOf[parse.MenuParseService].getName).to[parse.MenuParseService]
    bind[ParseService].annotatedWithName(classOf[parse.ShopParseService].getName).to[parse.ShopParseService]
    bind[FetchService].annotatedWithName(classOf[fetch.MenuHttpClientFetchService].getName).to[fetch.MenuHttpClientFetchService]
    bind[FetchService].annotatedWithName(classOf[fetch.ShopHttpClientFetchService].getName).to[fetch.ShopHttpClientFetchService]
  }

}
