package com.youleligou.eleme.repos.elasticsearch

import com.google.inject.Inject
import com.youleligou.core.reps.{CassandraRepo, ElasticSearchRepo}
import com.youleligou.eleme.daos.RestaurantDao
import com.youleligou.eleme.models.Restaurant
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class RestaurantRepo @Inject()(val sparkContext: SparkContext) extends ElasticSearchRepo[Restaurant] {
  override val index: String = "eleme"
  override val typ: String   = "restaurant"
}
