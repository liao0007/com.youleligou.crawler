package com.youleligou.eleme.daos

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Date

import com.youleligou.eleme.models.{Category, Food}

case class CategoryDao(
    id: Long,
    typ: Int,
    isActivity: Option[Boolean] = None,
    activity: Option[String] = None,
    description: String,
    iconUrl: String,
    name: String,
    createdAt: Date = Timestamp.valueOf(LocalDateTime.now())
)

object CategoryDao {
  implicit def fromModel(model: Category): CategoryDao = CategoryDao(
    id = model.id,
    typ = model.typ,
    isActivity = model.isActivity,
    activity = model.activity,
    description = model.description,
    iconUrl = model.iconUrl,
    name = model.name
  )

  implicit def fromModel(source: Seq[Category])(implicit converter: Category => CategoryDao): Seq[CategoryDao] =
    source map converter

  implicit def toModel(dao: CategoryDao): Category = Category(
    id = dao.id,
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
}
