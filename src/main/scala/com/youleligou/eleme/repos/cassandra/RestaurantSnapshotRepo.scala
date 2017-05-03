package com.youleligou.eleme.repos.cassandra

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.RestaurantSnapshotDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class RestaurantSnapshotRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[RestaurantSnapshotDao] {
  val keyspace: String = "eleme"
  val table: String    = "restaurant_snapshots"
}
