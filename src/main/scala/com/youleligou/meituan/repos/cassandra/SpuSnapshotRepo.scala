package com.youleligou.meituan.repos.cassandra

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.meituan.daos.SpuSnapshotDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class SpuSnapshotRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[SpuSnapshotDao] {
  val keyspace: String = "meituan"
  val table: String    = "spu_snapshots"
}
