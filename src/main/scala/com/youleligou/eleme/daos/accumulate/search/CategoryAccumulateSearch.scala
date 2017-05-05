package com.youleligou.eleme.daos.accumulate.search

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Date

import com.youleligou.eleme.models.{Category, Food}

case class CategoryAccumulateSearch(
    id: Long,
    typ: Int,
    isActivity: Option[Boolean] = None,
    activity: Option[String] = None,
    description: String,
    iconUrl: String,
    name: String,
    createdAt: Date = Timestamp.valueOf(LocalDateTime.now())
)

object CategoryAccumulateSearch {

  implicit def fromModel(model: Category): CategoryAccumulateSearch = CategoryAccumulateSearch(
    id = model.id,
    typ = model.typ,
    isActivity = model.isActivity,
    activity = model.activity,
    description = model.description,
    iconUrl = model.iconUrl,
    name = model.name
  )
  implicit def fromModel(source: Seq[Category])(implicit converter: Category => CategoryAccumulateSearch): Seq[CategoryAccumulateSearch] =
    source map converter

  implicit def toModel(dao: CategoryAccumulateSearch): Category = Category(
    id = dao.id,
    typ = dao.typ,
    isActivity = dao.isActivity,
    activity = dao.activity,
    description = dao.description,
    iconUrl = dao.iconUrl,
    name = dao.name,
    foods = Seq.empty[Food]
  )

  implicit def toModel(source: Seq[CategoryAccumulateSearch])(implicit converter: CategoryAccumulateSearch => Category): Seq[Category] =
    source map converter
}
