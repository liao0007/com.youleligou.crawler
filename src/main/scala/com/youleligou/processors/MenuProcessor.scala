package com.youleligou.processors

import com.datastax.spark.connector._
import com.google.inject.Inject
import com.youleligou.eleme.daos.FoodSnapshotDao
import org.apache.spark.SparkContext

/**
  * Created by liangliao on 4/5/17.
  */
class MenuProcessor @Inject()(sparkContext: SparkContext,
                              foodSnapshotRepo: com.youleligou.eleme.repos.cassandra.FoodSnapshotRepo,
                              restaurantRepo: com.youleligou.eleme.repos.cassandra.RestaurantRepo,
                              categoryRepo: com.youleligou.eleme.repos.cassandra.CategoryRepo,
                              foodSnapshotSearchRepo: com.youleligou.eleme.repos.elasticsearch.FoodSnapshotRepo) {

  def reindex()= {

  }

  def run(): Unit = {

    sparkContext
      .cassandraTable[FoodSnapshotDao]("eleme", "food_snapshots")
      .filter(_.monthSales > 0)
      .groupBy(_.name)
      .map { grouped =>
        grouped._1 -> grouped._2.map(_.monthSales).sum
      }
      .sortBy(_._2)
      .collect() foreach { sorted =>
      println(sorted._1 + "," + sorted._2)
    }

  }

}
