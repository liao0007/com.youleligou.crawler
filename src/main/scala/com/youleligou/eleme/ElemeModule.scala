package com.youleligou.eleme

import com.google.inject._
import com.youleligou.core.modules.GuiceAkkaActorRefProvider
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.crawler.modules.{DaoModule, _}
import com.youleligou.crawler.services.{FetchService, ParseService}
import com.youleligou.eleme.daos.{FoodSnapshotDaoSearch, _}
import com.youleligou.eleme.services._
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class ElemeModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  override def configure() {
    /*
    cassandra
     */
    bind[CassandraRepo[RestaurantDao]].to[repos.cassandra.RestaurantRepo]
    bind[CassandraRepo[CategoryDao]].to[repos.cassandra.CategoryRepo]

    bind[CassandraRepo[RestaurantSnapshotDao]].to[repos.cassandra.RestaurantSnapshotRepo]
    bind[CassandraRepo[CategorySnapshotDao]].to[repos.cassandra.CategorySnapshotRepo]
    bind[CassandraRepo[FoodSnapshotDao]].to[repos.cassandra.FoodSnapshotRepo]
    bind[CassandraRepo[FoodSkuSnapshotDao]].to[repos.cassandra.FoodSkuSnapshotRepo]

    /*
    es
     */
    bind[ElasticSearchRepo[RestaurantSnapshotDaoSearch]].to[repos.elasticsearch.RestaurantSnapshotRepo]
    bind[ElasticSearchRepo[FoodSnapshotDaoSearch]].to[repos.elasticsearch.FoodSnapshotRepo]

    /*
    services
     */
    bind[ParseService].annotatedWithName(classOf[parse.MenuParseService].getName).to[parse.MenuParseService]
    bind[ParseService].annotatedWithName(classOf[parse.RestaurantsParseService].getName).to[parse.RestaurantsParseService]
    bind[FetchService].annotatedWithName(classOf[fetch.MenuHttpClientFetchService].getName).to[fetch.MenuHttpClientFetchService]
    bind[FetchService].annotatedWithName(classOf[fetch.RestaurantsHttpClientFetchService].getName).to[fetch.RestaurantsHttpClientFetchService]
  }
}
