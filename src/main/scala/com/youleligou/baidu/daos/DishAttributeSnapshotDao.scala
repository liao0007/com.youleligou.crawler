package com.youleligou.baidu.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.baidu.models.DishAttribute
import com.youleligou.core.daos.SnapshotDao

case class DishAttributeSnapshotDao(
    createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
    shopId: Long,
    categoryId: Long,
    itemId: Long,
    dishAttrId: Long,
    //PK
    id: String,
    name: String,
    price: Float,
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends SnapshotDao

object DishAttributeSnapshotDao {

  implicit def fromModel(model: DishAttribute)(implicit dishSnapshotDao: DishSnapshotDao): DishAttributeSnapshotDao = DishAttributeSnapshotDao(
    shopId = dishSnapshotDao.shopId,
    categoryId = dishSnapshotDao.categoryId,
    itemId = dishSnapshotDao.itemId,
    dishAttrId = model.dishAttrId,
    //PK
    id = model.id,
    name = model.name,
    price = model.price
  )
  implicit def fromModel(source: Seq[DishAttribute])(implicit converter: DishAttribute => DishAttributeSnapshotDao,
                                                     dishSnapshotDao: DishSnapshotDao): Seq[DishAttributeSnapshotDao] =
    source map converter

  implicit def toModel(dao: DishAttributeSnapshotDao): DishAttribute = DishAttribute(
    id = dao.id,
    dishAttrId = dao.dishAttrId,
    name = dao.name,
    price = dao.price
  )

  implicit def toModel(source: Seq[DishAttributeSnapshotDao])(implicit converter: DishAttributeSnapshotDao => DishAttribute): Seq[DishAttribute] =
    source map converter

}
