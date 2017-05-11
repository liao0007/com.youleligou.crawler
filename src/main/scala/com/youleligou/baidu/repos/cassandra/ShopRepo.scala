package com.youleligou.baidu.repos.cassandra

import com.datastax.spark.connector._
import com.google.inject.Inject
import com.youleligou.baidu.daos.ShopDao
import com.youleligou.core.reps.CassandraRepo
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 25/4/17.
  */
class ShopRepo @Inject()(val sparkContext: SparkContext) extends CassandraRepo[ShopDao] {
  val keyspace: String = "baidu"
  val table: String    = "shops"

  def findById(shopId: Long): Option[ShopDao] =
    sparkContext.cassandraTable[ShopDao](keyspace, table).where("shop_id = ?", shopId).collect.headOption
  def allShopIds(): Seq[Long] = sparkContext.cassandraTable[Long](keyspace, table).select("shop_id").collect().toSeq
}
