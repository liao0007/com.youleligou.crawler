package com.youleligou.baidu.repos.cassandra

import com.google.inject.Inject
import com.youleligou.baidu.daos.DishSnapshotDao
import com.youleligou.core.reps.CassandraRepo
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class DishSnapshotRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[DishSnapshotDao] {
  val keyspace: String = "baidu"
  val table: String    = "dish_snapshots"
}
