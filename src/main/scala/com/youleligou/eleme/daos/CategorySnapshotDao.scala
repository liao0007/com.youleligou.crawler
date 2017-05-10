package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao
import com.youleligou.eleme.models.{Category, Food}

case class CategorySnapshotDao(
    createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
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
) extends SnapshotDao

object CategorySnapshotDao {
  implicit def fromModel(model: Category)(implicit restaurantDao: RestaurantDao): CategorySnapshotDao = CategorySnapshotDao(
    restaurantId = restaurantDao.restaurantId,
    categoryId = model.id,
    typ = model.typ,
    isActivity = model.isActivity,
    activity = model.activity,
    description = model.description,
    iconUrl = model.iconUrl,
    name = model.name
  )
  implicit def fromModel(source: Seq[Category])(implicit converter: Category => CategorySnapshotDao,
                                                restaurantDao: RestaurantDao): Seq[CategorySnapshotDao] =
    source map converter

  implicit def toModel(dao: CategorySnapshotDao): Category = Category(
    id = dao.categoryId,
    typ = dao.typ,
    isActivity = dao.isActivity,
    activity = dao.activity,
    description = dao.description,
    iconUrl = dao.iconUrl,
    name = dao.name,
    foods = Seq.empty[Food]
  )
  implicit def toModel(source: Seq[CategorySnapshotDao])(implicit converter: CategorySnapshotDao => Category): Seq[Category] =
    source map converter
}
