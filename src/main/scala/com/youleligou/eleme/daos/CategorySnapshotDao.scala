package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao
import com.youleligou.eleme.models.{Category, Food}

case class CategorySnapshotDao(
    id: Long,
    restaurantId: Long,
    typ: Int,
    isActivity: Option[Boolean] = None,
    activity: Option[String] = None,
    description: String,
    iconUrl: String,
    name: String,
    createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends SnapshotDao

object CategorySnapshotDao {
  implicit def fromModel(model: Category)(implicit restaurantDao: RestaurantDao): CategorySnapshotDao = CategorySnapshotDao(
    id = model.id,
    restaurantId = restaurantDao.id,
    typ = model.typ,
    isActivity = model.isActivity,
    activity = model.activity,
    description = model.description,
    iconUrl = model.iconUrl,
    name = model.name
  )
  implicit def fromModel(source: Seq[Category])(implicit converter: Category => CategorySnapshotDao): Seq[CategorySnapshotDao] =
    source map converter

  implicit def toModel(dao: CategorySnapshotDao): Category = Category(
    id = dao.id,
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
