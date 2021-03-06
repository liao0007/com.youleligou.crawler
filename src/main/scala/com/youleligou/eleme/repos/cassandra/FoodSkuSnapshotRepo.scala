package com.youleligou.eleme.repos.cassandra

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.FoodSkuSnapshotDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class FoodSkuSnapshotRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[FoodSkuSnapshotDao] {
  val keyspace: String = "eleme"
  val table: String    = "food_sku_snapshots"
}
