package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.LocalDateTime

import com.youleligou.core.daos.Dao
import com.youleligou.eleme.models.{Category, Food}

case class CategoryDao(
    restaurantId: Long,
    categoryId: Long,
    //PK
    typ: Int,
    isActivity: Option[Boolean] = None,
    activity: Option[String] = None,
    description: String,
    iconUrl: String,
    name: String,
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends Dao

object CategoryDao {
  implicit def fromModel(model: Category)(implicit restaurantDao: RestaurantDao): CategoryDao = CategoryDao(
    restaurantId = restaurantDao.restaurantId,
    categoryId = model.id,
    typ = model.typ,
    isActivity = model.isActivity,
    activity = model.activity,
    description = model.description,
    iconUrl = model.iconUrl,
    name = model.name
  )
  implicit def fromModel(source: Seq[Category])(implicit converter: Category => CategoryDao, restaurantDao: RestaurantDao): Seq[CategoryDao] =
    source map converter

  implicit def toModel(dao: CategoryDao): Category = Category(
    id = dao.categoryId,
    typ = dao.typ,
    isActivity = dao.isActivity,
    activity = dao.activity,
    description = dao.description,
    iconUrl = dao.iconUrl,
    name = dao.name,
    foods = Seq.empty[Food]
  )
  implicit def toModel(source: Seq[CategoryDao])(implicit converter: CategoryDao => Category): Seq[Category] =
    source map converter

  implicit def fromSnapshot(snapshot: CategorySnapshotDao): CategoryDao = CategoryDao(
    categoryId = snapshot.categoryId,
    restaurantId = snapshot.restaurantId,
    typ = snapshot.typ,
    isActivity = snapshot.isActivity,
    activity = snapshot.activity,
    description = snapshot.description,
    iconUrl = snapshot.iconUrl,
    name = snapshot.name,
    createdAt = snapshot.createdAt
  )

  implicit def fromSnapshot(source: Seq[CategorySnapshotDao])(implicit converter: CategorySnapshotDao => CategoryDao): Seq[CategoryDao] =
    source map converter
}
