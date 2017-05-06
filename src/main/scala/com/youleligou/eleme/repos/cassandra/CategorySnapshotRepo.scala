package com.youleligou.eleme.repos.cassandra

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.CategorySnapshotDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class CategorySnapshotRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[CategorySnapshotDao] {
  val keyspace: String = "eleme"
  val table: String    = "category_snapshots"
}
