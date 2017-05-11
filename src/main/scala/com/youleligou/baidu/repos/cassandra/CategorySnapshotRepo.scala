package com.youleligou.baidu.repos.cassandra

import com.google.inject.Inject
import com.youleligou.baidu.daos.CategorySnapshotDao
import com.youleligou.core.reps.CassandraRepo
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class CategorySnapshotRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[CategorySnapshotDao] {
  val keyspace: String = "baidu"
  val table: String    = "category_snapshots"
}
