package com.youleligou.processors

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Date

import com.datastax.spark.connector._
import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.eleme.daos._
import com.youleligou.eleme.models.{Category, FoodSku, Restaurant}
import org.apache.spark.SparkContext
import org.joda.time.LocalDate

/**
  * Created by liangliao on 4/5/17.
  */
class MenuProcessor @Inject()(sparkContext: SparkContext,
                              foodSnapshotRepo: com.youleligou.eleme.repos.cassandra.FoodSnapshotRepo,
                              restaurantRepo: com.youleligou.eleme.repos.cassandra.RestaurantRepo,
                              categoryRepo: com.youleligou.eleme.repos.cassandra.CategoryRepo,
                              foodSnapshotSearchRepo: com.youleligou.eleme.repos.elasticsearch.FoodSnapshotRepo) extends LazyLogging{

  def reindex(): Unit = {
    Seq("2017-05-06", "2017-05-07", "2017-05-08", "2017-05-09", "2017-05-10") foreach { date =>
      sparkContext.cassandraTable[RestaurantSnapshotDao]("eleme", "restaurant_snapshots").where("created_date = ?", date) foreach { (restaurantSnapshotDao: RestaurantSnapshotDao) =>
        sparkContext.cassandraTable[CategorySnapshotDao]("eleme", "category_snapshots").where("created_date = ? and restaurant_id = ?", date, restaurantSnapshotDao.id).collect() foreach { (categorySnapshotDao: CategorySnapshotDao) =>
          sparkContext.cassandraTable[FoodSnapshotDao]("eleme", "food_snapshots").where("created_date = ? and category_id = ?", date, categorySnapshotDao.id).collect() foreach { (foodSnapshotDao: FoodSnapshotDao) =>
            val foodSkuSnapshotDaos: Seq[FoodSkuSnapshotDao] = sparkContext.cassandraTable[FoodSkuSnapshotDao]("eleme", "food_sku_snapshots").where("created_date = ? and item_id = ?", date, foodSnapshotDao.itemId).collect().toSeq
            implicit val foodSkus : Seq[FoodSku] = foodSkuSnapshotDaos
            implicit val restaurant: RestaurantDaoSearch = restaurantSnapshotDao
            implicit val category: CategoryDaoSearch = categorySnapshotDao

            val foodSnapshotDaoSearch : FoodSnapshotDaoSearch = foodSnapshotDao
            logger.info(foodSnapshotDaoSearch.id)
//            foodSnapshotSearchRepo.save(foodSnapshotDaoSearch.copy(id = foodSnapshotDaoSearch.itemId + "-" + date))
          }
        }
      }
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
