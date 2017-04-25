package com.youleligou.eleme.repos.cassandra

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.RestaurantDao
import org.apache.spark.SparkContext
import com.datastax.spark.connector._
import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.RestaurantSnapshotDao
import org.apache.spark.SparkContext

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by liangliao on 25/4/17.
  */
class RestaurantRepo @Inject()(val keyspace: String = "eleme", val table: String = "restaurants", val sparkContext: SparkContext)
    extends CassandraRepo[RestaurantDao] {
  def all(): Future[Seq[RestaurantDao]] = Future {
    sparkContext.cassandraTable[RestaurantDao](keyspace, table).collect().toSeq
  }
}
