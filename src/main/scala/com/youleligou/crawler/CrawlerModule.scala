package com.youleligou.crawler

import akka.actor.ActorSystem
import com.datastax.driver.core.{HostDistance, PoolingOptions}
import com.google.inject._
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.core.reps.Repo
import com.youleligou.crawler.daos.JobDao
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.ParseService
import com.youleligou.eleme.services.{food, restaurant}
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class CrawlerModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  override def configure() {
    bind[Repo[JobDao]].annotatedWithName(classOf[food.ParseService].getName).to[food.ParseService]
  }
}
