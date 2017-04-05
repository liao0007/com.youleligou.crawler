package com.youleligou.crawler.services

import com.youleligou.crawler.actors.AbstractFetchActor.Fetch

import scala.concurrent.Future

trait InjectService {
  def initSeed(): Future[Int]

  def generateFetch(seed: Int): Fetch
}
