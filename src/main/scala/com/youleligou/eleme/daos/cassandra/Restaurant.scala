package com.youleligou.eleme.daos.cassandra

import com.outworkers.phantom.dsl._
import com.youleligou.crawler.daos.cassandra.CrawlerJob
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

import scala.concurrent.Future

case class Restaurant(
    id: Long,
    address: String,
    averageCost: String,
    description: String,
    deliveryFee: Float,
    minimumOrderAmount: Float,
    imagePath: String,
    isNew: Boolean,
    isPremium: Boolean,
    latitude: Float,
    longitude: Float,
    name: String,
    phone: String,
    promotionInfo: String,
    rating: Float,
    ratingCount: Int,
    recentOrderNum: Int,
    licensesNumber: Option[String],
    companyName: Option[String],
    status: Int,
    createdAt: DateTime = DateTime.now()
)

object Restaurant {
  implicit val restaurantReads: Reads[Restaurant] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "address").readWithDefault[String]("").map(_.trim) and
      (JsPath \ "average_cost").readWithDefault[String]("").map(_.trim) and
      (JsPath \ "description").readWithDefault[String]("").map(_.trim) and
      (JsPath \ "float_delivery_fee").readWithDefault[Float](0f) and
      (JsPath \ "float_minimum_order_amount").readWithDefault[Float](0f) and
      (JsPath \ "image_path").readWithDefault[String]("").map(_.trim) and
      (JsPath \ "is_new").readWithDefault[Boolean](false) and
      (JsPath \ "is_premium").readWithDefault[Boolean](false) and
      (JsPath \ "latitude").readWithDefault[Float](0f) and
      (JsPath \ "longitude").readWithDefault[Float](0f) and
      (JsPath \ "name").readWithDefault[String]("").map(_.trim) and
      (JsPath \ "phone").readWithDefault[String]("").map(_.trim) and
      (JsPath \ "promotion_info").readWithDefault[String]("").map(_.trim) and
      (JsPath \ "rating").readWithDefault[Float](0f) and
      (JsPath \ "rating_count").readWithDefault[Int](0) and
      (JsPath \ "recent_order_num").readWithDefault[Int](0) and
      (JsPath \ "identification" \ "licenses_number").readNullable[String] and
      (JsPath \ "identification" \ "company_name").readNullable[String] and
      (JsPath \ "status").readWithDefault[Int](0) and
      (JsPath \ "created_at").readWithDefault(DateTime.now())
  )(Restaurant.apply _)
}

abstract class Restaurants extends CassandraTable[Restaurants, Restaurant] with RootConnector {
  object id                 extends LongColumn(this) with PartitionKey
  object address            extends StringColumn(this)
  object averageCost        extends StringColumn(this)
  object description        extends StringColumn(this)
  object deliveryFee        extends FloatColumn(this)
  object minimumOrderAmount extends FloatColumn(this)
  object imagePath          extends StringColumn(this)
  object isNew              extends BooleanColumn(this)
  object isPremium          extends BooleanColumn(this)
  object latitude           extends FloatColumn(this)
  object longitude          extends FloatColumn(this)
  object name               extends StringColumn(this)
  object phone              extends StringColumn(this)
  object promotionInfo      extends StringColumn(this)
  object rating             extends FloatColumn(this)
  object ratingCount        extends IntColumn(this)
  object recentOrderNum     extends IntColumn(this)
  object licensesNumber     extends OptionalStringColumn(this)
  object companyName        extends OptionalStringColumn(this)
  object status             extends IntColumn(this)
  object createdAt          extends DateTimeColumn(this) with ClusteringOrder with Descending

  def batchInsertOrUpdate(restaurants: Seq[CrawlerJob]): Future[ResultSet] =
    Batch.unlogged
      .add(restaurants.map { restaurant =>
        store(restaurant)
      }.iterator)
      .future()

  def insertOrUpdate(restaurants: Seq[Restaurant]): Seq[Future[ResultSet]] = restaurants.map(insertOrUpdate)

  def insertOrUpdate(restaurant: Restaurant): Future[ResultSet] = store(restaurant).future()

  def all: Future[List[Restaurant]] = select.fetch()
}
