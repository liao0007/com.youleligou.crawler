package com.youleligou.meituan.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao
import com.youleligou.meituan.modals.{Sku, Spu}

/**
  * Created by liangliao on 8/5/17.
  */
case class SpuSnapshotDao( // food
                          poiId: Long,
                          tagId: Long,
                          spuId: Long,
                          name: String,
                          minPrice: Float,
                          praiseNum: Int,
                          treadNum: Int,
                          praiseNumNew: Int,
                          description: Option[String],
                          picture: String,
                          monthSaled: Int,
                          status: Int,
                          createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
                          createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now()))
    extends SnapshotDao

object SpuSnapshotDao {

  implicit def fromModel(model: Spu)(implicit foodTagDao: FoodTagDao): SpuSnapshotDao = SpuSnapshotDao(
    poiId = foodTagDao.poiId,
    spuId = model.id,
    name = model.name,
    minPrice = model.minPrice,
    praiseNum = model.praiseNum,
    treadNum = model.treadNum,
    praiseNumNew = model.praiseNumNew,
    description = model.description,
    picture = model.picture,
    monthSaled = model.monthSaled,
    status = model.status,
    tagId = model.tag
  )
  implicit def fromModel(source: Seq[Spu])(implicit converter: Spu => SpuSnapshotDao, foodTagDao: FoodTagDao): Seq[SpuSnapshotDao] =
    source map converter

  implicit def toModel(dao: SpuSnapshotDao): Spu = Spu(
    id = dao.spuId,
    name = dao.name,
    minPrice = dao.minPrice,
    praiseNum = dao.praiseNum,
    treadNum = dao.treadNum,
    praiseNumNew = dao.praiseNumNew,
    description = dao.description,
    picture = dao.picture,
    monthSaled = dao.monthSaled,
    status = dao.status,
    tag = dao.tagId,
    skus = Seq.empty[Sku]
  )
  implicit def toModel(source: Seq[SpuSnapshotDao])(implicit converter: SpuSnapshotDao => Spu): Seq[Spu] =
    source map converter

}
