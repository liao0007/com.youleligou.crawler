package com.youleligou.meituan.reps.cassandra

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.RestaurantSnapshotDao
import com.youleligou.meituan.daos.PoiSnapshotDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class PoiSnapshotRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[PoiSnapshotDao] {
  val keyspace: String = "meituan"
  val table: String    = "poi_snapshots"
}
