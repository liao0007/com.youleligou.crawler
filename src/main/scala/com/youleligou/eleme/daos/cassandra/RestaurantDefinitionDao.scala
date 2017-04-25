package com.youleligou.eleme.daos.cassandra

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.crawler.daos.JobDao
import com.youleligou.eleme.models.Restaurant
import org.apache.spark.SparkContext
import org.joda.time.DateTime

import scala.concurrent.Future

case class RestaurantDefinitionDao(
    id: Long,
    address: String,
    latitude: Float,
    longitude: Float,
    name: String,
    licensesNumber: Option[String] = None,
    companyName: Option[String] = None,
    createdAt: DateTime = DateTime.now()
)

class RestaurantDefinitionRepo @Inject()(val keyspace: String = "eleme", val table: String = "restaurantdefinitions", val sparkContext: SparkContext)
    extends CassandraRepo[RestaurantDefinitionDao]
