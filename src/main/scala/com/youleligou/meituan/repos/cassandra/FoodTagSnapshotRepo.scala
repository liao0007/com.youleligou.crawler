package com.youleligou.meituan.repos.cassandra

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.CategorySnapshotDao
import com.youleligou.meituan.daos.FoodTagSnapshotDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class FoodTagSnapshotRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[FoodTagSnapshotDao] {
  val keyspace: String = "meituan"
  val table: String    = "food_tag_snapshots"
}
