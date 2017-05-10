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

  def findByWmPoiViewId(id: Long): Option[PoiDao] =
    sparkContext.cassandraTable[PoiDao](keyspace, table).where("wm_poi_view_id = ?", id).collect.headOption
  def allWmPoiViewIds(): Seq[Long] = sparkContext.cassandraTable[Long](keyspace, table).select("wm_poi_view_id").take(2).toSeq
}
