package com.youleligou.core.daos

import java.util.Date

/**
  * Created by liangliao on 6/5/17.
  */
trait Dao extends Serializable {
  def createdAt: Date
}
