package com.youleligou.eleme.repos.mysql

import com.github.tototoshi.slick.MySQLJodaSupport._
import com.google.inject.Inject
import com.youleligou.core.reps.MysqlRepo
import com.youleligou.eleme.daos.RestaurantSnapshotDao
import org.joda.time.{DateTime, LocalDate}
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal
/**
  * Created by liangliao on 25/4/17.
  */
class RestaurantDaoRepo @Inject()(val schema: String = "cancan", val table: String = "restaurant", val database: Database)
    extends MysqlRepo[RestaurantSnapshotDao] {
  val RestaurantDaos: TableQuery[RestaurantDaoTable] = TableQuery[RestaurantDaoTable]

  def find(id: Long): Future[Option[RestaurantSnapshotDao]] =
    database.run(RestaurantDaos.filter(_.id === id).result.headOption) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        None
    }

  def delete(id: Long): Future[Int] =
    database.run(RestaurantDaos.filter(_.id === id).delete) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }

  def all(): Future[Seq[RestaurantSnapshotDao]] =
    database.run(RestaurantDaos.to[Seq].result) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        List.empty[RestaurantSnapshotDao]
    }

  def allIds(): Future[List[Long]] =
    database.run(RestaurantDaos.map(_.id).to[List].result) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        List.empty[Long]
    }

  def save(restaurant: RestaurantSnapshotDao): Future[Any] =
    database.run(RestaurantDaos += restaurant) recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
    }

  def save(restaurants: Seq[RestaurantSnapshotDao]): Future[Any] =
    database.run(RestaurantDaos ++= restaurants) recover {
      case NonFatal(x) if !x.getMessage.contains("Duplicate entry") =>
        logger.warn(x.getMessage)
    }

  def insertOrUpdate(restaurant: RestaurantSnapshotDao): Future[Int] =
    database.run {
      RestaurantDaos.insertOrUpdate(restaurant)
    } recover {
      case NonFatal(x) =>
        logger.warn(x.getMessage)
        0
    }
}

class RestaurantDaoTable(tag: Tag) extends Table[RestaurantSnapshotDao](tag, "restaurant") {
  def id                 = column[Long]("id", O.PrimaryKey)
  def address            = column[String]("address")
  def averageCost        = column[String]("average_cost")
  def description        = column[String]("description")
  def deliveryFee        = column[Float]("delivery_fee")
  def minimumOrderAmount = column[Float]("minimum_order_amount")
  def imagePath          = column[String]("image_path")
  def isNew              = column[Boolean]("is_new")
  def isPremium          = column[Boolean]("is_premium")
  def latitude           = column[Float]("latitude")
  def longitude          = column[Float]("longitude")
  def name               = column[String]("name")
  def phone              = column[String]("phone")
  def promotionInfo      = column[String]("promotion_info")
  def rating             = column[Float]("rating")
  def ratingCount        = column[Int]("rating_count")
  def recentOrderNum     = column[Int]("recent_order_num")
  def licensesNumber     = column[String]("licenses_number")
  def companyName        = column[String]("company_name")
  def status             = column[Int]("status")
  def createdDate        = column[LocalDate]("created_date")
  def createdAt          = column[DateTime]("created_at")

  def * =
    (id,
     address,
     averageCost.?,
     description,
     deliveryFee,
     minimumOrderAmount,
     imagePath,
     isNew,
     isPremium,
     latitude,
     longitude,
     name,
     phone.?,
     promotionInfo,
     rating,
     ratingCount,
     recentOrderNum,
     licensesNumber.?,
     companyName.?,
     status,
     createdDate,
     createdAt) <> ((RestaurantSnapshotDao.apply _).tupled, RestaurantSnapshotDao.unapply)

}
