package com.youleligou.eleme.repos.cassandra

import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.{FoodSnapshotDao, RestaurantDao, RestaurantSnapshotDao}
import org.apache.spark.SparkContext
import com.datastax.spark.connector._
import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import org.apache.spark.SparkContext

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by liangliao on 25/4/17.
  */
class FoodSnapshotRepo @Inject()(val keyspace: String = "eleme", val table: String = "food_snapshots", val sparkContext: SparkContext)
    extends CassandraRepo[FoodSnapshotDao] {

  def all(): Future[Seq[FoodSnapshotDao]] = Future {
    sparkContext.cassandraTable[FoodSnapshotDao](keyspace, table).collect().toSeq
  }
}
