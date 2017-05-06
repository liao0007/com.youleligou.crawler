package com.youleligou.core.daos

import java.sql.Date

/**
  * Created by liangliao on 6/5/17.
  */
trait SnapshotDao extends Dao {
  def createdDate: Date
}
