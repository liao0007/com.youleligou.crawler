package com.youleligou.eleme.repos.cassandra

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.cassandra.FoodDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class FoodRepo @Inject()(val keyspace: String = "eleme", val table: String = "foods", val sparkContext: SparkContext) extends CassandraRepo[FoodDao]
