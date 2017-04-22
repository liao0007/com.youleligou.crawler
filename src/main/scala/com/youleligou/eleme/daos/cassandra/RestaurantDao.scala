package com.youleligou.eleme.daos.cassandra

import com.outworkers.phantom.dsl._
import com.youleligou.crawler.daos.cassandra.CrawlerJob
import com.youleligou.eleme.models.Restaurant
import org.joda.time.{DateTime, LocalDate}

import scala.concurrent.Future

case class RestaurantDao(
    id: Long,
    address: String,
    averageCost: Option[String],
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
    licensesNumber: Option[String] = None,
    companyName: Option[String] = None,
    status: Int,
    createdDate: LocalDate = LocalDate.now(),
    createdAt: DateTime = DateTime.now()
)

object RestaurantDao {
  implicit def fromModel(model: Restaurant): RestaurantDao = RestaurantDao(
    id = model.id,
    address = model.address,
    averageCost = model.averageCost,
    description = model.description,
    deliveryFee = model.deliveryFee,
    minimumOrderAmount = model.minimumOrderAmount,
    imagePath = model.imagePath,
    isNew = model.isNew,
    isPremium = model.isPremium,
    latitude = model.latitude,
    longitude = model.longitude,
    name = model.name,
    phone = model.phone,
    promotionInfo = model.promotionInfo,
    rating = model.rating,
    ratingCount = model.ratingCount,
    recentOrderNum = model.recentOrderNum,
    licensesNumber = model.identification.flatMap(_.licensesNumber),
    companyName = model.identification.flatMap(_.companyName),
    status = model.status
  )

  implicit def convertSeq(source: Seq[Restaurant])(implicit converter: Restaurant => RestaurantDao): Seq[RestaurantDao] = source map converter
}

abstract class Restaurants extends CassandraTable[Restaurants, RestaurantDao] with RootConnector {
  object id                 extends LongColumn(this) with PartitionKey
  object address            extends StringColumn(this)
  object averageCost        extends OptionalStringColumn(this)
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
  object createdDate        extends LocalDateColumn(this) with ClusteringOrder with Descending
  object createdAt          extends DateTimeColumn(this)

  def batchInsertOrUpdate(restaurants: Seq[CrawlerJob]): Future[ResultSet] =
    Batch.unlogged
      .add(restaurants.map { restaurant =>
        store(restaurant)
      }.iterator)
      .future()

  def insertOrUpdate(restaurants: Seq[RestaurantDao]): Seq[Future[ResultSet]] = restaurants.map(insertOrUpdate)

  def insertOrUpdate(restaurant: RestaurantDao): Future[ResultSet] = store(restaurant).future()

  def all(): Future[List[RestaurantDao]] = select.fetch()

  def allIds(): Future[List[Long]] = select(_.id).fetch()
}
