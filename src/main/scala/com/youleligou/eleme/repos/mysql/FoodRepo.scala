package com.youleligou.eleme.repos.mysql

import com.github.tototoshi.slick.MySQLJodaSupport._
import com.google.inject.Inject
import com.youleligou.core.reps.MysqlRepo
import com.youleligou.eleme.daos.FoodSnapshotDao
import org.joda.time.DateTime
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal
/**
  * Created by liangliao on 25/4/17.
  */
class FoodDaoRepo @Inject()(val schema: String = "cancan", val table: String = "foods", val database: Database) extends MysqlRepo[FoodSnapshotDao] {
  val FoodDaos: TableQuery[FoodDaoTable] = TableQuery[FoodDaoTable]

  def find(itemId: Long): Future[Option[FoodSnapshotDao]] =
    database.run(FoodDaos.filter(_.itemId === itemId).result.headOption) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }

  def delete(itemId: Long): Future[Int] =
    database.run(FoodDaos.filter(_.itemId === itemId).delete) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

  def all(): Future[Seq[FoodSnapshotDao]] =
    database.run(FoodDaos.to[Seq].result) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        List.empty[FoodSnapshotDao]
    }

  def save(restaurant: FoodSnapshotDao): Future[Any] =
    database.run(FoodDaos += restaurant) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
    }

  def save(restaurants: Seq[FoodSnapshotDao]): Future[Any] =
    database.run(FoodDaos ++= restaurants) recover {
      case NonFatal(x) if !x.getMessage.contains("Duplicate entry") =>
        logger.warn(x.getMessage)
    }

  def insertOrUpdate(restaurant: FoodSnapshotDao): Future[Int] =
    database.run {
      FoodDaos.insertOrUpdate(restaurant)
    } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

}

class FoodDaoTable(tag: Tag) extends Table[FoodSnapshotDao](tag, "foods") {
  def itemId       = column[Long]("item_id", O.PrimaryKey)
  def restaurantId = column[Long]("restaurant_id")
  def categoryId   = column[Long]("category_id")
  def name         = column[String]("name")
  def description  = column[String]("description")
  def monthSales   = column[Int]("month_sales")
  def rating       = column[Float]("rating")
  def ratingCount  = column[Int]("rating_count")
  def satisfyCount = column[Int]("satisfy_count")
  def satisfyRate  = column[Float]("satisfy_rate")
  def createdAt    = column[DateTime]("created_at")

  def * =
    (itemId, restaurantId, categoryId, name, description, monthSales, rating, ratingCount, satisfyCount, satisfyRate, createdAt) <> ((FoodSnapshotDao.apply _).tupled, FoodSnapshotDao.unapply)

}
