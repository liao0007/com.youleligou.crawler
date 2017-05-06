package com.youleligou.processors

import com.datastax.spark.connector._
import com.datastax.spark.connector.rdd.CassandraRDD
import com.google.inject.Inject
import com.youleligou.eleme.daos.{RestaurantSnapshotDaoSearch, _}
import org.apache.spark.SparkContext

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by liangliao on 4/5/17.
  */
class RestaurantProcessor @Inject()(sparkContext: SparkContext,
                                    restaurantSnapshotRepo: com.youleligou.eleme.repos.cassandra.RestaurantSnapshotRepo,
                                    restaurantSnapshotSearchRepo: com.youleligou.eleme.repos.elasticsearch.RestaurantSnapshotRepo) {

  def reindex(): Future[Any] = {
    restaurantSnapshotRepo.rddAll() flatMap { (restaurantSnapshotDaoRdd: CassandraRDD[RestaurantSnapshotDao]) =>
      val restaurantSearchRdd = restaurantSnapshotDaoRdd.map { (restaurantSnapshotDao: RestaurantSnapshotDao) =>
        val restaurantSnapshotDaoSearch: RestaurantSnapshotDaoSearch = restaurantSnapshotDao
        restaurantSnapshotDaoSearch
      }
      restaurantSnapshotSearchRepo.save(restaurantSearchRdd)
    }
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
