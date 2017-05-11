package com.youleligou.baidu.daos

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.youleligou.baidu.models.{Category, Dish}
import com.youleligou.core.daos.SnapshotDao

case class CategorySnapshotDao(
    createdDate: java.util.Date = java.sql.Date.valueOf(LocalDate.now()),
    shopId: Long,
    categoryId: Long,
    //PK
    catalog: String,
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends SnapshotDao

object CategorySnapshotDao {
  implicit def fromModel(model: Category)(implicit shopDao: ShopDao): CategorySnapshotDao = CategorySnapshotDao(
    shopId = shopDao.shopId,
    categoryId = model.categoryId,
    //PK
    catalog = model.catalog
  )
  implicit def fromModel(source: Seq[Category])(implicit converter: Category => CategorySnapshotDao, shopDao: ShopDao): Seq[CategorySnapshotDao] =
    source map converter

  implicit def toModel(dao: CategorySnapshotDao): Category = Category(
    categoryId = dao.categoryId,
    //PK
    catalog = dao.catalog,
    dishes = Seq.empty[Dish]
  )
  implicit def toModel(source: Seq[CategorySnapshotDao])(implicit converter: CategorySnapshotDao => Category): Seq[Category] =
    source map converter
}
