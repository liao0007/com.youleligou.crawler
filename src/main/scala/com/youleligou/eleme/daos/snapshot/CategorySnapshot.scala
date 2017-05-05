package com.youleligou.eleme.daos.snapshot

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.eleme.daos.snapshot.search.CategorySnapshotSearch
import com.youleligou.eleme.models.{Category, Food}

case class CategorySnapshot(
    id: Long,
    typ: Int,
    isActivity: Option[Boolean] = None,
    activity: Option[String] = None,
    description: String,
    iconUrl: String,
    name: String,
    createdDate: java.sql.Date = java.sql.Date.valueOf(LocalDate.now()),
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
)

object CategorySnapshot {
  implicit def fromModel(model: Category): CategorySnapshot = CategorySnapshot(
    id = model.id,
    typ = model.typ,
    isActivity = model.isActivity,
    activity = model.activity,
    description = model.description,
    iconUrl = model.iconUrl,
    name = model.name
  )
  implicit def fromModel(source: Seq[Category])(implicit converter: Category => CategorySnapshot): Seq[CategorySnapshot] =
    source map converter

  implicit def toModel(dao: CategorySnapshot): Category = Category(
    id = dao.id,
    typ = dao.typ,
    isActivity = dao.isActivity,
    activity = dao.activity,
    description = dao.description,
    iconUrl = dao.iconUrl,
    name = dao.name,
    foods = Seq.empty[Food]
  )
  implicit def toModel(source: Seq[CategorySnapshot])(implicit converter: CategorySnapshot => Category): Seq[Category] =
    source map converter

  /*
  search <-> dao
   */
  implicit def fromSearch(search: CategorySnapshotSearch): CategorySnapshot = {
    CategorySnapshot(
      id = search.id,
      typ = search.typ,
      isActivity = search.isActivity,
      activity = search.activity,
      description = search.description,
      iconUrl = search.iconUrl,
      name = search.name,
      createdDate = search.createdDate,
      createdAt = search.createdAt
    )
  }
  implicit def fromSearch(source: Seq[CategorySnapshotSearch])(
      implicit converter: CategorySnapshotSearch => CategorySnapshot): Seq[CategorySnapshot] =
    source map converter

  implicit def toSearch(dao: CategorySnapshot): CategorySnapshotSearch =
    CategorySnapshotSearch(
      id = dao.id,
      typ = dao.typ,
      isActivity = dao.isActivity,
      activity = dao.activity,
      description = dao.description,
      iconUrl = dao.iconUrl,
      name = dao.name,
      createdDate = dao.createdDate,
      createdAt = dao.createdAt
    )
  implicit def toSearch(source: Seq[CategorySnapshot])(implicit converter: CategorySnapshot => CategorySnapshotSearch): Seq[CategorySnapshotSearch] =
    source map converter
}
