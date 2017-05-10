package com.youleligou.meituan.daos

import java.sql.Timestamp
import java.time.LocalDateTime

import com.youleligou.core.daos.Dao
import com.youleligou.meituan.modals.{FoodTag, Spu}

/**
  * Created by liangliao on 8/5/17.
  */
case class FoodTagDao( // category
                       poiId: Long,
                       tagId: Long,
                       name: String,
                       icon: String,
                       typ: Int,
                       createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now()))
    extends Dao

object FoodTagDao {
  implicit def fromModel(model: FoodTag)(implicit poiDao: PoiDao): FoodTagDao = FoodTagDao(
    tagId = model.tag,
    poiId = poiDao.poiId,
    name = model.name,
    icon = model.icon,
    typ = model.typ
  )
  implicit def fromModel(source: Seq[FoodTag])(implicit converter: FoodTag => FoodTagDao): Seq[FoodTagDao] =
    source map converter

  implicit def toModel(dao: FoodTagDao): FoodTag = FoodTag(
    tag = dao.tagId,
    name = dao.name,
    icon = dao.icon,
    typ = dao.typ,
    spus = Seq.empty[Spu]
  )
  implicit def toModel(source: Seq[FoodTagDao])(implicit converter: FoodTagDao => FoodTag): Seq[FoodTag] =
    source map converter
}
