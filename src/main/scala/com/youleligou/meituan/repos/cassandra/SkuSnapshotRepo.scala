package com.youleligou.meituan.repos.cassandra

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.FoodSkuSnapshotDao
import com.youleligou.meituan.daos.SkuSnapshotDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class SkuSnapshotRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[SkuSnapshotDao] {
  val keyspace: String = "meituan"
  val table: String    = "sku_snapshots"
}
