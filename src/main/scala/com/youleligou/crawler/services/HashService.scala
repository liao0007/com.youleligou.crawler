package com.youleligou.crawler.services

/**
  * Created by liangliao on 1/4/17.
  */
trait HashService {
  def hash(text: String): String
}
