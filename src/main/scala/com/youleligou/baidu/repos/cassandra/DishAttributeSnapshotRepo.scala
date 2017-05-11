package com.youleligou.baidu.repos.cassandra

import com.google.inject.Inject
import com.youleligou.baidu.daos.DishAttributeSnapshotDao
import com.youleligou.core.reps.CassandraRepo
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class DishAttributeSnapshotRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[DishAttributeSnapshotDao] {
  val keyspace: String = "baidu"
  val table: String    = "dish_attribute_snapshots"
}
