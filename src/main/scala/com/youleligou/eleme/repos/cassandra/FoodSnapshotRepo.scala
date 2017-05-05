package com.youleligou.eleme.repos.cassandra

import com.google.inject.Inject
import com.datastax.spark.connector._
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.accumulate.RestaurantAccumulate
import com.youleligou.eleme.daos.snapshot.FoodSnapshot
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
  * Created by liangliao on 25/4/17.
  */
class FoodSnapshotRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[FoodSnapshot] {
  val keyspace: String = "eleme"
  val table: String    = "food_snapshots"

  def findByRestaurantId(id: Long): Array[FoodSnapshot] =
    sparkContext.cassandraTable[FoodSnapshot](keyspace, table).where("restaurant_id = ?", id).collect()
}
