package com.youleligou.eleme.repos.cassandra

import com.datastax.spark.connector._
import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.RestaurantDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class RestaurantRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[RestaurantDao] {
  val keyspace: String = "eleme"
  val table: String    = "restaurants"

  def findById(restaurantId: Long): Option[RestaurantDao] =
    sparkContext.cassandraTable[RestaurantDao](keyspace, table).where("restaurant_id = ?", restaurantId).collect.headOption
  def allRestaurantIds(): Seq[Long] = sparkContext.cassandraTable[Long](keyspace, table).select("restaurant_id").collect().toSeq
}
