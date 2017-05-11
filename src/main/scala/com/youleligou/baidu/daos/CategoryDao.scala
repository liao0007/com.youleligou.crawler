package com.youleligou.baidu.daos

import java.sql.Timestamp
import java.time.LocalDateTime

import com.youleligou.baidu.models._
import com.youleligou.core.daos.Dao

case class CategoryDao(
    shopId: Long,
    categoryId: Long,
    //PK
    catalog: String,
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends Dao

object CategoryDao {
  implicit def fromModel(model: Category)(implicit shopDao: ShopDao): CategoryDao = CategoryDao(
    shopId = shopDao.shopId,
    categoryId = model.categoryId,
    //PK
    catalog = model.catalog
  )
  implicit def fromModel(source: Seq[Category])(implicit converter: Category => CategoryDao, shopDao: ShopDao): Seq[CategoryDao] =
    source map converter

  implicit def toModel(dao: CategoryDao): Category = Category(
    categoryId = dao.categoryId,
    //PK
    catalog = dao.catalog,
    dishes = Seq.empty[Dish]
  )
  implicit def toModel(source: Seq[CategoryDao])(implicit converter: CategoryDao => Category): Seq[Category] =
    source map converter

  implicit def fromSnapshot(snapshot: CategorySnapshotDao): CategoryDao = CategoryDao(
    shopId = snapshot.shopId,
    categoryId = snapshot.categoryId,
    //PK
    catalog = snapshot.catalog
  )

  implicit def fromSnapshot(source: Seq[CategorySnapshotDao])(implicit converter: CategorySnapshotDao => CategoryDao): Seq[CategoryDao] =
    source map converter
}
