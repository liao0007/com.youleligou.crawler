package com.youleligou.eleme

import com.google.inject._
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.ParseService
import com.youleligou.eleme.daos.{FoodSnapshotDao, RestaurantDao, RestaurantSearchDao, RestaurantSnapshotDao}
import com.youleligou.eleme.services.{foods, restaurants}
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class ElemeModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  override def configure() {
    bind[CassandraRepo[FoodSnapshotDao]].to[repos.cassandra.FoodSnapshotRepo]
    bind[CassandraRepo[RestaurantSnapshotDao]].to[repos.cassandra.RestaurantSnapshotRepo]
    bind[CassandraRepo[RestaurantDao]].to[repos.cassandra.RestaurantRepo]

    bind[ElasticSearchRepo[RestaurantSearchDao]].to[repos.elasticsearch.RestaurantRepo]

    bind[ParseService].annotatedWithName(classOf[foods.ParseService].getName).to[foods.ParseService]
    bind[ParseService].annotatedWithName(classOf[restaurants.ParseService].getName).to[restaurants.ParseService]
  }
}
