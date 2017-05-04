package com.youleligou.eleme

import com.google.inject._
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.ParseService
import com.youleligou.eleme.daos._
import com.youleligou.eleme.services.{menu, restaurants}
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class ElemeModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  override def configure() {
    bind[CassandraRepo[FoodSnapshotDao]].to[repos.cassandra.FoodSnapshotRepo]
    bind[CassandraRepo[RestaurantSnapshotDao]].to[repos.cassandra.RestaurantSnapshotRepo]
    bind[CassandraRepo[RestaurantDao]].to[repos.cassandra.RestaurantRepo]

    bind[ElasticSearchRepo[RestaurantSearch]].to[repos.elasticsearch.RestaurantRepo]
    bind[ElasticSearchRepo[FoodSnapshotSearch]].to[repos.elasticsearch.FoodSnapshotRepo]

    bind[ParseService].annotatedWithName(classOf[menu.ParseService].getName).to[menu.ParseService]
    bind[ParseService].annotatedWithName(classOf[restaurants.ParseService].getName).to[restaurants.ParseService]
  }
}
