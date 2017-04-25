package com.youleligou.eleme.repos.cassandra

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
class RestaurantSnapshotRepo @Inject()(val keyspace: String = "eleme", val table: String = "restaurant_snapshots", val sparkContext: SparkContext)
    extends CassandraRepo[RestaurantSnapshotDao] {

  def allIds(): Seq[Long] = sparkContext.cassandraTable[Long](keyspace, table).select("id").collect().toSeq

  def all(): Future[Seq[RestaurantSnapshotDao]] = Future {
    sparkContext.cassandraTable[RestaurantSnapshotDao](keyspace, table).collect().toSeq
  }

}
