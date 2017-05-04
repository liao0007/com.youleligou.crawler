package com.youleligou.processors

import com.datastax.spark.connector._
import com.datastax.spark.connector.rdd.CassandraRDD
import com.google.inject.Inject
import com.youleligou.eleme.daos.{FoodSnapshotDao, RestaurantDao, RestaurantSearch}
import com.youleligou.eleme.models.Restaurant
import org.apache.spark.SparkContext

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by liangliao on 4/5/17.
  */
class RestaurantProcessor @Inject()(sparkContext: SparkContext,
                                    restaurantRepo: com.youleligou.eleme.repos.cassandra.RestaurantRepo,
                                    restaurantSearchRepo: com.youleligou.eleme.repos.elasticsearch.RestaurantRepo) {

  def reindex(): Future[Any] = {
    restaurantRepo.allRdd().flatMap { (restaurantDaoRdd: CassandraRDD[RestaurantDao]) =>
      val restaurantSearchRdd = restaurantDaoRdd.map { (restaurantDao: RestaurantDao) =>
        val restaurant: Restaurant             = restaurantDao
        val restaurantSearch: RestaurantSearch = restaurant
        restaurantSearch
      }
      restaurantSearchRepo.save(restaurantSearchRdd)
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
