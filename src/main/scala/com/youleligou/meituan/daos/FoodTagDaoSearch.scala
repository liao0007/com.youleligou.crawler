package com.youleligou.meituan.daos

import java.sql.Timestamp
import java.time.LocalDateTime

import com.youleligou.core.daos.Dao

case class FoodTagDaoSearch(
    tag: Long,
    poiId: Long,
    name: String,
    icon: String,
    typ: Int,
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends Dao

object FoodTagDaoSearch {
  implicit def fromDao(dao: FoodTagDao): FoodTagDaoSearch =
    FoodTagDaoSearch(
      tag = dao.tag,
      poiId = dao.poiId,
      name = dao.name,
      icon = dao.icon,
      typ = dao.typ,
      createdAt = dao.createdAt
    )
  implicit def fromDao(source: Seq[FoodTagDao])(implicit converter: FoodTagDao => FoodTagDaoSearch): Seq[FoodTagDaoSearch] =
    source map converter

  implicit def toDao(search: FoodTagDaoSearch): FoodTagDao = FoodTagDao(
    tag = search.tag,
    poiId = search.poiId,
    name = search.name,
    icon = search.icon,
    typ = search.typ,
    createdAt = search.createdAt
  )
  implicit def toDao(source: Seq[FoodTagDaoSearch])(implicit converter: FoodTagDaoSearch => FoodTagDao): Seq[FoodTagDao] =
    source map converter

}
