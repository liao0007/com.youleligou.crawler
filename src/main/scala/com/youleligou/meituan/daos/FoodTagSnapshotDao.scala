package com.youleligou.meituan.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao
import com.youleligou.meituan.modals.{FoodTag, Spu}

/**
  * Created by liangliao on 8/5/17.
  */
case class FoodTagSnapshotDao( // category
                              tag: Long,
                              poiId: Long,
                              name: String,
                              icon: String,
                              typ: Int,
                              createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
                              createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now()))
    extends SnapshotDao

object FoodTagSnapshotDao {
  implicit def fromModel(model: FoodTag)(implicit poiDao: PoiDao): FoodTagSnapshotDao = FoodTagSnapshotDao(
    tag = model.tag,
    poiId = poiDao.id,
    name = model.name,
    icon = model.icon,
    typ = model.typ
  )
  implicit def fromModel(source: Seq[FoodTag])(implicit converter: FoodTag => FoodTagSnapshotDao): Seq[FoodTagSnapshotDao] =
    source map converter

  implicit def toModel(dao: FoodTagSnapshotDao): FoodTag = FoodTag(
    tag = dao.tag,
    name = dao.name,
    icon = dao.icon,
    typ = dao.typ,
    spus = Seq.empty[Spu]
  )
  implicit def toModel(source: Seq[FoodTagSnapshotDao])(implicit converter: FoodTagSnapshotDao => FoodTag): Seq[FoodTag] =
    source map converter
}
