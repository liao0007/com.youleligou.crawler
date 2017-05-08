package com.youleligou.meituan.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao
import com.youleligou.meituan.modals.{Sku, Spu}

/**
  * Created by liangliao on 8/5/17.
  */
case class SpuSnapshotDao( // food
                          id: Long,
                          name: String,
                          minPrice: Float,
                          priseNum: Int,
                          treadNum: Int,
                          priseNumNew: Int,
                          description: Option[String],
                          picture: String,
                          monthSaled: Int,
                          status: Int,
                          tag: Long,
                          createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
                          createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now()))
    extends SnapshotDao

object SpuSnapshotDao {

  implicit def fromModel(model: Spu): SpuSnapshotDao = SpuSnapshotDao(
    id = model.id,
    name = model.name,
    minPrice = model.minPrice,
    priseNum = model.priseNum,
    treadNum = model.treadNum,
    priseNumNew = model.priseNumNew,
    description = model.description,
    picture = model.picture,
    monthSaled = model.monthSaled,
    status = model.status,
    tag = model.tag
  )
  implicit def fromModel(source: Seq[Spu])(implicit converter: Spu => SpuSnapshotDao): Seq[SpuSnapshotDao] =
    source map converter

  implicit def toModel(dao: SpuSnapshotDao): Spu = Spu(
    id = dao.id,
    name = dao.name,
    minPrice = dao.minPrice,
    priseNum = dao.priseNum,
    treadNum = dao.treadNum,
    priseNumNew = dao.priseNumNew,
    description = dao.description,
    picture = dao.picture,
    monthSaled = dao.monthSaled,
    status = dao.status,
    tag = dao.tag,
    skus = Seq.empty[Sku]
  )
  implicit def toModel(source: Seq[SpuSnapshotDao])(implicit converter: SpuSnapshotDao => Spu): Seq[Spu] =
    source map converter

}
