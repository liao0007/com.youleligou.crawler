package com.youleligou.eleme.repos.cassandra

import com.datastax.spark.connector._
import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.RestaurantSnapshotDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class RestaurantRepo @Inject()(val keyspace: String = "eleme", val table: String = "restaurants", val sparkContext: SparkContext)
    extends CassandraRepo[RestaurantSnapshotDao] {

  def allIds(): Seq[Long] = sparkContext.cassandraTable[Long](keyspace, table).select("id").collect().toSeq

}
