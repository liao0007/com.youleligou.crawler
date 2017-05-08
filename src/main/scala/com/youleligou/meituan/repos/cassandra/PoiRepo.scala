package com.youleligou.meituan.repos.cassandra

import com.datastax.spark.connector._
import com.google.inject.Inject
import com.youleligou.core.reps.CassandraRepo
import com.youleligou.meituan.daos.PoiDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class PoiRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[PoiDao] {
  val keyspace: String = "meituan"
  val table: String    = "pois"

  def findById(id: Long): Option[PoiDao] = sparkContext.cassandraTable[PoiDao](keyspace, table).where("id = ?", id).collect.headOption
  def allIds(): Seq[Long]                = sparkContext.cassandraTable[Long](keyspace, table).select("id").collect().toSeq
}
