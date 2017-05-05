package com.youleligou.eleme.daos.snapshot.search

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.eleme.models.{Category, Food}

/**
  * Created by liangliao on 6/5/17.
  */
case class CategorySnapshotSearch(
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

object CategorySnapshotSearch {
  implicit def fromModel(model: Category): CategorySnapshotSearch = CategorySnapshotSearch(
    id = model.id,
    typ = model.typ,
    isActivity = model.isActivity,
    activity = model.activity,
    description = model.description,
    iconUrl = model.iconUrl,
    name = model.name
  )
  implicit def convertDaoSeq(source: Seq[Category])(implicit converter: Category => CategorySnapshotSearch): Seq[CategorySnapshotSearch] =
    source map converter

  implicit def toModel(dao: CategorySnapshotSearch): Category = Category(
    id = dao.id,
    typ = dao.typ,
    isActivity = dao.isActivity,
    activity = dao.activity,
    description = dao.description,
    iconUrl = dao.iconUrl,
    name = dao.name,
    foods = Seq.empty[Food]
  )
  implicit def convertModelSeq(source: Seq[CategorySnapshotSearch])(implicit converter: CategorySnapshotSearch => Category): Seq[Category] =
    source map converter
}
