package com.youleligou.crawler.models

/**
  * 种子类
  *
  * @param url 种子url
  */
case class Seed(url: String) {
  override def toString: String = url + "\n"
}
