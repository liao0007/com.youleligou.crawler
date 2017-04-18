package com.youleligou.eleme

import akka.actor.ActorSystem
import com.datastax.driver.core.{HostDistance, PoolingOptions}
import com.google.inject._
import com.google.inject.name.Named
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoints}
import com.typesafe.config.Config
import com.youleligou.crawler.modules._
import com.youleligou.crawler.services.ParseService
import com.youleligou.eleme.daos.cassandra.ElemeDatabase
import com.youleligou.eleme.services.{food, restaurant}
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by liangliao on 31/3/17.
  */
class ElemeModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  @Provides
  @Singleton
  @Named(daos.cassandra.keyspaces.Eleme)
  def provideElemeCassandraConnection(config: Config, system: ActorSystem): CassandraConnection = {
    import com.github.andr83.scalaconfig._

    val poolingOptions: PoolingOptions = new PoolingOptions()
    poolingOptions
      .setMaxRequestsPerConnection(HostDistance.LOCAL, config.getInt("db.cassandra.maxRequestsPerLocalConnection"))
      .setMaxRequestsPerConnection(HostDistance.REMOTE, config.getInt("db.cassandra.maxRequestsPerRemoteConnection"))
      .setMaxQueueSize(config.getInt("db.cassandra.maxQueueSize"))

    ContactPoints(config.as[Seq[String]]("db.cassandra.contactPoints"))
      .withClusterBuilder(
        _.withPoolingOptions(poolingOptions)
      )
      .keySpace(daos.cassandra.keyspaces.Eleme)
  }

  @Provides
  @Singleton
  @Named(daos.cassandra.keyspaces.Eleme)
  def provideElemeCassandraDatabaseProvider(system: ActorSystem, elemeDatabase: ElemeDatabase): ElemeDatabase = {
    system.registerOnTermination({
      elemeDatabase.shutdown()
    })
    elemeDatabase
  }

  override def configure() {
    bind[ParseService].annotatedWithName(classOf[food.ParseService].getName).to[food.ParseService]
    bind[ParseService].annotatedWithName(classOf[restaurant.ParseService].getName).to[restaurant.ParseService]
  }
}
