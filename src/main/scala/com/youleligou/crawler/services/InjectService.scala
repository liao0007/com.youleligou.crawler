package com.youleligou.crawler.services

import com.youleligou.crawler.actors.AbstractFetchActor.Fetch
import com.youleligou.crawler.actors.AbstractInjectActor.SeedInitialized

import scala.concurrent.Future

trait InjectService {
  def initSeed(): Future[SeedInitialized]
  def generateFetch(seed: Int): Fetch
}
