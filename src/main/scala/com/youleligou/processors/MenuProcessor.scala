package com.youleligou.processors

import com.datastax.spark.connector._
import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import com.youleligou.eleme.daos.{FoodSnapshotDaoSearch, _}
import com.youleligou.eleme.models.FoodSku
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.joda.time.LocalDate

/**
  * Created by liangliao on 4/5/17.
  */
class MenuProcessor @Inject()(sparkContext: SparkContext, foodSnapshotSearchRepo: com.youleligou.eleme.repos.elasticsearch.FoodSnapshotRepo)
    extends LazyLogging {
  /*
  def reindex2(): Unit = {
    Seq("2017-05-06", "2017-05-07", "2017-05-08", "2017-05-09", "2017-05-10") foreach { date =>
      sparkContext.cassandraTable[RestaurantSnapshotDao]("eleme", "restaurant_snapshots").where("created_date = ?", date).collect() foreach { (restaurantSnapshotDao: RestaurantSnapshotDao) =>
        sparkContext.cassandraTable[CategorySnapshotDao]("eleme", "category_snapshots").where("created_date = ? and restaurant_id = ?", date, restaurantSnapshotDao.id).collect() foreach { (categorySnapshotDao: CategorySnapshotDao) =>
          sparkContext.cassandraTable[FoodSnapshotDao]("eleme", "food_snapshots").where("created_date = ? and category_id = ?", date, categorySnapshotDao.id).collect() foreach { (foodSnapshotDao: FoodSnapshotDao) =>
            val foodSkuSnapshotDaos: Seq[FoodSkuSnapshotDao] = sparkContext.cassandraTable[FoodSkuSnapshotDao]("eleme", "food_sku_snapshots").where("created_date = ? and item_id = ?", date, foodSnapshotDao.itemId).collect().toSeq
            implicit val foodSkus : Seq[FoodSku] = foodSkuSnapshotDaos
            implicit val restaurant: RestaurantDaoSearch = restaurantSnapshotDao
            implicit val categorySearch: CategoryDaoSearch = categorySnapshotDao

            val foodSnapshotDaoSearch : FoodSnapshotDaoSearch = foodSnapshotDao
            foodSnapshotDaoSearch.copy(id = foodSnapshotDaoSearch.itemId + "-" + date)
            foodSnapshotSearchRepo.save(foodSnapshotDaoSearch)
          }
        }
      }
    }
  }
   */


  def reindex(): Unit = {
    import java.text.SimpleDateFormat
    val formatter = new SimpleDateFormat("yyyy-MM-dd")
    case class Day(created_date: java.util.Date)
    val days    = Seq("2017-05-06", "2017-05-07", "2017-05-08", "2017-05-09", "2017-05-10").map(formatter.parse).map(Day)
    val daysRdd = sparkContext.parallelize(days)
    val count   = daysRdd.joinWithCassandraTable("eleme", "restaurant_snapshots").on(SomeColumns("created_date")).count()
    println(s"count is $count")
  }

}
