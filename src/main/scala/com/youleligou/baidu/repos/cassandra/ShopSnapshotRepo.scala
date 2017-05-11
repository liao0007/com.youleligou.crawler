package com.youleligou.baidu.repos.cassandra

import com.google.inject.Inject
import com.youleligou.baidu.daos.ShopSnapshotDao
import com.youleligou.core.reps.CassandraRepo
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class ShopSnapshotRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[ShopSnapshotDao] {
  val keyspace: String = "baidu"
  val table: String    = "shop_snapshots"
}
