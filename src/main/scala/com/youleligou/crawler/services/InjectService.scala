package com.youleligou.crawler.services

import com.typesafe.scalalogging.LazyLogging
import com.youleligou.crawler.actors.AbstractFetchActor.Fetch
import com.youleligou.crawler.actors.AbstractInjectActor.SeedInitialized

import scala.concurrent.Future

trait InjectService extends LazyLogging {
  def initSeed(): Future[SeedInitialized]
  def generateFetch(seed: Int): Fetch
}
