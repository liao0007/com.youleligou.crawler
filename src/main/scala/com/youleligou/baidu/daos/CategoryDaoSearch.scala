package com.youleligou.baidu.daos

import java.sql.Timestamp
import java.time.LocalDateTime

import com.youleligou.core.daos.Dao

case class CategoryDaoSearch(
    shopId: Long,
    categoryId: Long,
    //PK
    catalog: String,
    createdAt: java.util.Date = Timestamp.valueOf(LocalDateTime.now())
) extends Dao

object CategoryDaoSearch {
  implicit def fromDao(dao: CategoryDao): CategoryDaoSearch =
    CategoryDaoSearch(
      shopId = dao.shopId,
      categoryId = dao.categoryId,
      //PK
      catalog = dao.catalog,
      createdAt = dao.createdAt
    )
  implicit def fromDao(source: Seq[CategoryDao])(implicit converter: CategoryDao => CategoryDaoSearch): Seq[CategoryDaoSearch] =
    source map converter

  implicit def toDao(search: CategoryDaoSearch): CategoryDao = CategoryDao(
    shopId = search.shopId,
    categoryId = search.categoryId,
    //PK
    catalog = search.catalog,
    createdAt = search.createdAt
  )
  implicit def toDao(source: Seq[CategoryDaoSearch])(implicit converter: CategoryDaoSearch => CategoryDao): Seq[CategoryDao] =
    source map converter

  implicit def fromSnapshotDao(snapshot: CategorySnapshotDao): CategoryDaoSearch =
    CategoryDaoSearch(
      shopId = snapshot.shopId,
      categoryId = snapshot.categoryId,
      //PK
      catalog = snapshot.catalog,
      createdAt = snapshot.createdAt
    )
  implicit def fromSnapshotDao(source: Seq[CategorySnapshotDao])(
      implicit converter: CategorySnapshotDao => CategoryDaoSearch): Seq[CategoryDaoSearch] =
    source map converter

}
