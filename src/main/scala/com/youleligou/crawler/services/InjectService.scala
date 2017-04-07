package com.youleligou.crawler.services

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractFetchActor.FetchUrl
import com.youleligou.crawler.actors.AbstractInjectActor.Initialized

import scala.concurrent.Future

trait InjectService extends LazyLogging {
  def initSeed(): Future[Int]
  def generateFetch(seed: Int): FetchUrl
}
