package com.youleligou.eleme.repos.cassandra

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.RestaurantDefinitionDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class RestaurantDefinitionRepo @Inject()(val keyspace: String = "eleme", val table: String = "restaurantdefinitions", val sparkContext: SparkContext)
    extends CassandraRepo[RestaurantDefinitionDao]
