package com.youleligou.eleme

import com.google.inject._
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.ParseService
import com.youleligou.eleme.daos._
import com.youleligou.eleme.daos.accumulate.search.RestaurantAccumulateSearch
import com.youleligou.eleme.daos.accumulate.{CategoryAccumulate, RestaurantAccumulate}
import com.youleligou.eleme.daos.snapshot.search.FoodSnapshotSearch
import com.youleligou.eleme.daos.snapshot.{FoodSnapshot, RestaurantSnapshot}
import com.youleligou.eleme.services.{menu, restaurants}
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class ElemeModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  override def configure() {
    bind[CassandraRepo[FoodSnapshot]].to[repos.cassandra.FoodSnapshotRepo]
    bind[CassandraRepo[RestaurantAccumulateSearch]].to[repos.cassandra.RestaurantSnapshotRepo]
    bind[CassandraRepo[RestaurantAccumulateSearch]].to[repos.cassandra.RestaurantRepo]
    bind[CassandraRepo[CategoryAccumulate]].to[repos.cassandra.CategoryRepo]

    bind[ElasticSearchRepo[RestaurantAccumulateSearch]].to[repos.elasticsearch.RestaurantRepo]
    bind[ElasticSearchRepo[FoodSnapshot]].to[repos.elasticsearch.FoodSnapshotRepo]

    bind[ParseService].annotatedWithName(classOf[menu.ParseService].getName).to[menu.ParseService]
    bind[ParseService].annotatedWithName(classOf[restaurants.ParseService].getName).to[restaurants.ParseService]
  }
}
