package com.youleligou.meituan

import com.google.inject._
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.ParseService
import com.youleligou.meituan.services._
import com.youleligou.meituan.daos._
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class MeituanModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  override def configure() {
    /*
    cassandra
     */
    bind[CassandraRepo[PoiDao]].to[repos.cassandra.PoiRepo]
    bind[CassandraRepo[FoodTagDao]].to[repos.cassandra.FoodTagRepo]

    bind[CassandraRepo[PoiSnapshotDao]].to[repos.cassandra.PoiSnapshotRepo]
    bind[CassandraRepo[FoodTagSnapshotDao]].to[repos.cassandra.FoodTagSnapshotRepo]
    bind[CassandraRepo[SpuSnapshotDao]].to[repos.cassandra.SpuSnapshotRepo]
    bind[CassandraRepo[SkuSnapshotDao]].to[repos.cassandra.SkuSnapshotRepo]

    /*
    es
     */
    bind[ElasticSearchRepo[PoiSnapshotDaoSearch]].to[repos.elasticsearch.PoiSnapshotRepo]
    bind[ElasticSearchRepo[SpuSnapshotDaoSearch]].to[repos.elasticsearch.SpuSnapshotRepo]

    /*
    services
     */
    bind[ParseService].annotatedWithName(classOf[poiFilter.ParseService].getName).to[poiFilter.ParseService]
    bind[ParseService].annotatedWithName(classOf[poiFood.ParseService].getName).to[poiFood.ParseService]
  }
}
