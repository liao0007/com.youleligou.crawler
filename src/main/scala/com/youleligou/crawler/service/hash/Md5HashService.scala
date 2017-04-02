package com.youleligou.crawler.service.hash

import java.security.MessageDigest

import com.youleligou.crawler.service.HashService

/**
  * Created by liangliao on 1/4/17.
  */
class Md5HashService extends HashService {
  def hash(text: String): String = MessageDigest.getInstance("MD5").digest(text.getBytes).toString
}
