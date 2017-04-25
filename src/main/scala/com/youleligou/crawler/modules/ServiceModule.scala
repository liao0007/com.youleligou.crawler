package com.youleligou.crawler.modules

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject._
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.daos
import com.youleligou.crawler.services._
import com.youleligou.crawler.services.fetch.HttpClientFetchService
import com.youleligou.crawler.services.filter.DefaultFilterService
import com.youleligou.crawler.services.hash.Md5HashService
import com.youleligou.crawler.services.index.ElasticIndexService
import net.codingwell.scalaguice.ScalaModule
import org.apache.spark.{SparkConf, SparkContext}
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import redis.RedisClient
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._

import scala.collection.JavaConverters._

/**
  * Created by liangliao on 31/3/17.
  */
class ServiceModule extends AbstractModule with ScalaModule {

  @Provides
  @Singleton
  def provideRedisClient(config: Config)(implicit system: ActorSystem): RedisClient = {
    val redisConfig: Config = config.getConfig("cache.redis")
    val redisClient =
      RedisClient(host = redisConfig.getString("host"), port = redisConfig.getInt("port"))
    system.registerOnTermination({
      redisClient.shutdown()
    })
    redisClient
  }

  @Provides
  @Singleton
  @Named(daos.mysql.schemas.CanCan)
  def provideDatabaseCanCan(system: ActorSystem): MySQLProfile.backend.Database = {
    val database = Database.forConfig("db.cancan")
    system.registerOnTermination({
      database.close()
    })
    database
  }

  /*
  spark context provider
   */
  @Provides
  @Singleton
  def provideCrawlerSparkContext(config: Config, system: ActorSystem): SparkContext = {
    val conf = new SparkConf(true)
      .set("spark.cassandra.connection.host", config.getStringList("db.cassandra.contactPoints").asScala.mkString(","))
//      .set("spark.cassandra.auth.username", "cassandra")
//      .set("spark.cassandra.auth.password", "cassandra")

    val sc = new SparkContext(config.getString("spark.master"), config.getString("spark.appName"), conf)
    system.registerOnTermination({
      sc.stop()
    })
    sc
  }

  /*
   */
  @Provides
  @Singleton
  def provideActorMaterializer(implicit system: ActorSystem): ActorMaterializer = {
    ActorMaterializer()
  }

  @Provides
  @Singleton
  def provideStandaloneAhcWSClient(system: ActorSystem)(implicit actorMaterializer: ActorMaterializer): StandaloneAhcWSClient = {
    val standaloneAhcWSClient = StandaloneAhcWSClient()
    system.registerOnTermination({
      standaloneAhcWSClient.close()
    })
    standaloneAhcWSClient
  }

  override def configure() {
    bind[HashService].to[Md5HashService].asEagerSingleton()
    bind[FetchService].to[HttpClientFetchService].asEagerSingleton()
    bind[IndexService].to[ElasticIndexService].asEagerSingleton()
    bind[FilterService].to[DefaultFilterService].asEagerSingleton()
  }
}
