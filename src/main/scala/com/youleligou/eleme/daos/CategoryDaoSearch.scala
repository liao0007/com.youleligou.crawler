package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.LocalDateTime

import com.youleligou.core.daos.Dao

case class CategoryDaoSearch(
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

object CategoryDaoSearch {
  implicit def fromDao(dao: CategoryDao): CategoryDaoSearch =
    CategoryDaoSearch(
      categoryId = dao.categoryId,
      restaurantId = dao.restaurantId,
      typ = dao.typ,
      isActivity = dao.isActivity,
      activity = dao.activity,
      description = dao.description,
      iconUrl = dao.iconUrl,
      name = dao.name,
      createdAt = dao.createdAt
    )
  implicit def fromDao(source: Seq[CategoryDao])(implicit converter: CategoryDao => CategoryDaoSearch): Seq[CategoryDaoSearch] =
    source map converter

  implicit def toDao(search: CategoryDaoSearch): CategoryDao = CategoryDao(
    categoryId = search.categoryId,
    restaurantId = search.restaurantId,
    typ = search.typ,
    isActivity = search.isActivity,
    activity = search.activity,
    description = search.description,
    iconUrl = search.iconUrl,
    name = search.name,
    createdAt = search.createdAt
  )
  implicit def toDao(source: Seq[CategoryDaoSearch])(implicit converter: CategoryDaoSearch => CategoryDao): Seq[CategoryDao] =
    source map converter

  implicit def fromSnapshotDao(snapshot: CategorySnapshotDao): CategoryDaoSearch =
    CategoryDaoSearch(
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
  implicit def fromSnapshotDao(source: Seq[CategorySnapshotDao])(
      implicit converter: CategorySnapshotDao => CategoryDaoSearch): Seq[CategoryDaoSearch] =
    source map converter

}
