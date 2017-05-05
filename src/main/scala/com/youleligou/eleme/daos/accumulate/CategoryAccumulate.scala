package com.youleligou.eleme.daos.accumulate

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Date

import com.youleligou.eleme.daos.accumulate.search.CategoryAccumulateSearch
import com.youleligou.eleme.models.{Category, Food}

case class CategoryAccumulate(
    id: Long,
    typ: Int,
    isActivity: Option[Boolean] = None,
    activity: Option[String] = None,
    description: String,
    iconUrl: String,
    name: String,
    createdAt: Date = Timestamp.valueOf(LocalDateTime.now())
)

object CategoryAccumulate {
  implicit def fromModel(model: Category): CategoryAccumulate = CategoryAccumulate(
    id = model.id,
    typ = model.typ,
    isActivity = model.isActivity,
    activity = model.activity,
    description = model.description,
    iconUrl = model.iconUrl,
    name = model.name
  )
  implicit def fromModel(source: Seq[Category])(implicit converter: Category => CategoryAccumulate): Seq[CategoryAccumulate] =
    source map converter

  implicit def toModel(dao: CategoryAccumulate): Category = Category(
    id = dao.id,
    typ = dao.typ,
    isActivity = dao.isActivity,
    activity = dao.activity,
    description = dao.description,
    iconUrl = dao.iconUrl,
    name = dao.name,
    foods = Seq.empty[Food]
  )
  implicit def toModel(source: Seq[CategoryAccumulate])(implicit converter: CategoryAccumulate => Category): Seq[Category] =
    source map converter

  implicit def fromSearch(search: CategoryAccumulateSearch): CategoryAccumulate = CategoryAccumulate(
    id = search.id,
    typ = search.typ,
    isActivity = search.isActivity,
    activity = search.activity,
    description = search.description,
    iconUrl = search.iconUrl,
    name = search.name,
    createdAt = search.createdAt
  )
  implicit def fromSearch(source: Seq[CategoryAccumulateSearch])(
      implicit converter: CategoryAccumulateSearch => CategoryAccumulate): Seq[CategoryAccumulate] =
    source map converter

  implicit def toSearch(dao: CategoryAccumulate): CategoryAccumulateSearch = CategoryAccumulateSearch(
    id = dao.id,
    typ = dao.typ,
    isActivity = dao.isActivity,
    activity = dao.activity,
    description = dao.description,
    iconUrl = dao.iconUrl,
    name = dao.name,
    createdAt = dao.createdAt
  )
  implicit def toSearch(source: Seq[CategoryAccumulate])(
      implicit converter: CategoryAccumulate => CategoryAccumulateSearch): Seq[CategoryAccumulateSearch] =
    source map converter

}
