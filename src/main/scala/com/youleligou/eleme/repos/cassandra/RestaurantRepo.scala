package com.youleligou.eleme.repos.cassandra

import com.datastax.spark.connector._
import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.eleme.daos.RestaurantDao
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
  * Created by liangliao on 25/4/17.
  */
class RestaurantRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[RestaurantDao] {
  val keyspace: String = "eleme"
  val table: String    = "restaurants"

  def rddFindById(id: Long): RDD[RestaurantDao] = sparkContext.cassandraTable[RestaurantDao](keyspace, table).where("id = ?", id)
  def allIds(): Seq[Long]                       = sparkContext.cassandraTable[Long](keyspace, table).select("id").collect().toSeq
}
