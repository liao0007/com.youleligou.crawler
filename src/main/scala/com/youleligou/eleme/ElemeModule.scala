package com.youleligou.eleme

import com.google.inject._
import com.youleligou.core.reps.Repo
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.ParseService
import com.youleligou.eleme.daos.{FoodDao, RestaurantDao, RestaurantDefinitionDao}
import com.youleligou.eleme.repos.cassandra.{FoodRepo, RestaurantDefinitionRepo, RestaurantRepo}
import com.youleligou.eleme.services.{food, restaurant}
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class ElemeModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  override def configure() {
    bind[Repo[FoodDao]].to[FoodRepo]
    bind[Repo[RestaurantDao]].to[RestaurantRepo]
    bind[Repo[RestaurantDefinitionDao]].to[RestaurantDefinitionRepo]

    bind[ParseService].annotatedWithName(classOf[food.ParseService].getName).to[food.ParseService]
    bind[ParseService].annotatedWithName(classOf[restaurant.ParseService].getName).to[restaurant.ParseService]
  }
}
