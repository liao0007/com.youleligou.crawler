package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.LocalDateTime

import com.youleligou.core.daos.Dao

case class CategoryDaoSearch(
    id: Long,
    restaurantId: Long,
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
      id = dao.id,
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
    id = search.id,
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

}
