package com.youleligou.meituan.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.core.daos.SnapshotDao
import com.youleligou.meituan.modals.Sku

/**
  * Created by liangliao on 8/5/17.
  */
case class SkuSnapshotDao(
    poiId: Long,
    tagId: Long,
    spuId: Long,
    skuId: Long,
    spec: Option[String],
    description: Option[String],
    picture: String,
    price: Float,
    originPrice: Float,
    boxNum: Float,
    boxPrice: Float,
    minOrderCount: Int,
    status: Int,
    stock: Int,
    realStock: Int,
    activityStock: Int,
    restrict: Int,
    promotionInfo: Option[String],
    createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends SnapshotDao

object SkuSnapshotDao {

  implicit def fromModel(model: Sku)(implicit spuSnapshotDao: SpuSnapshotDao): SkuSnapshotDao = SkuSnapshotDao(
    poiId = spuSnapshotDao.poiId,
    tagId = spuSnapshotDao.tagId,
    spuId = spuSnapshotDao.spuId,
    skuId = model.id,
    spec = model.spec,
    description = model.description,
    picture = model.picture,
    price = model.price,
    originPrice = model.originPrice,
    boxNum = model.boxNum,
    boxPrice = model.boxPrice,
    minOrderCount = model.minOrderCount,
    status = model.status,
    stock = model.stock,
    realStock = model.realStock,
    activityStock = model.activityStock,
    restrict = model.restrict,
    promotionInfo = model.promotionInfo
  )
  implicit def fromModel(source: Seq[Sku])(implicit converter: Sku => SkuSnapshotDao, spuSnapshotDao: SpuSnapshotDao): Seq[SkuSnapshotDao] =
    source map converter

  implicit def toModel(dao: SkuSnapshotDao): Sku = Sku(
    id = dao.skuId,
    spec = dao.spec,
    description = dao.description,
    picture = dao.picture,
    price = dao.price,
    originPrice = dao.originPrice,
    boxNum = dao.boxNum,
    boxPrice = dao.boxPrice,
    minOrderCount = dao.minOrderCount,
    status = dao.status,
    stock = dao.stock,
    realStock = dao.realStock,
    activityStock = dao.activityStock,
    restrict = dao.restrict,
    promotionInfo = dao.promotionInfo
  )
  implicit def toModel(source: Seq[SkuSnapshotDao])(implicit converter: SkuSnapshotDao => Sku): Seq[Sku] =
    source map converter

}
