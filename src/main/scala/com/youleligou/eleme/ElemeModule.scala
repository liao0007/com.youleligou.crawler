package com.youleligou.eleme

import com.google.inject._
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.ParseService
import com.youleligou.eleme.daos.{FoodSnapshotDaoSearch, _}
import com.youleligou.eleme.services.{menu, restaurants}
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
    bind[ParseService].annotatedWithName(classOf[menu.ParseService].getName).to[menu.ParseService]
    bind[ParseService].annotatedWithName(classOf[restaurants.ParseService].getName).to[restaurants.ParseService]
  }
}
