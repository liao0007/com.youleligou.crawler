package com.youleligou.eleme.daos.cassandra

import com.outworkers.phantom.dsl._
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

import scala.concurrent.Future

case class Food(
    itemId: Long,
    restaurantId: Long,
    categoryId: Long,
    name: String,
    description: String,
    monthSales: Int,
    rating: Float,
    ratingCount: Int,
    satisfyCount: Int,
    satisfyRate: Float,
    createdAt: DateTime = DateTime.now()
)

object Food {
  implicit val restaurantReads: Reads[Food] = (
    (JsPath \ "item_id").read[String].map(_.toLong) and
      (JsPath \ "restaurant_id").read[Long] and
      (JsPath \ "category_id").read[Long] and
      (JsPath \ "name").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "month_sales").read[Int] and
      (JsPath \ "rating").read[Float] and
      (JsPath \ "rating_count").read[Int] and
      (JsPath \ "satisfy_count").read[Int] and
      (JsPath \ "satisfy_rate").read[Float] and
      (JsPath \ "created_at").readWithDefault(DateTime.now())
  )(Food.apply _)
}

abstract class Foods extends CassandraTable[Foods, Food] with RootConnector {
  object restaurantId extends LongColumn(this) with PartitionKey
  object itemId       extends LongColumn(this)
  object categoryId   extends LongColumn(this)
  object name         extends StringColumn(this)
  object description  extends StringColumn(this)
  object monthSales   extends IntColumn(this)
  object rating       extends FloatColumn(this)
  object ratingCount  extends IntColumn(this)
  object satisfyCount extends IntColumn(this)
  object satisfyRate  extends FloatColumn(this)
  object createdAt    extends DateTimeColumn(this) with ClusteringOrder with Descending

  def batchInsertOrUpdate(foods: Seq[Food]): Future[ResultSet] =
    Batch.unlogged
      .add(foods.map { food =>
        store(food)
      }.iterator)
      .future()

  def insertOrUpdate(foods: Seq[Food]): Seq[Future[ResultSet]] = foods.map(insertOrUpdate)

  def insertOrUpdate(food: Food): Future[ResultSet] = store(food).future()

  def all(): Future[List[Food]] = select.fetch()
}
