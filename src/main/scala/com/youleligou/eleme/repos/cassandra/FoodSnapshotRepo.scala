package com.youleligou.eleme.repos.cassandra

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.FoodSnapshotDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class FoodSnapshotRepo @Inject()(val keyspace: String = "eleme", val table: String = "food_snapshots", val sparkContext: SparkContext)
    extends CassandraRepo[FoodSnapshotDao]
