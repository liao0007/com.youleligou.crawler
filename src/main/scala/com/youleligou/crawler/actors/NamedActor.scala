package com.youleligou.crawler.actors

/**
  * A convenience trait for an actor companion object to extend to provide names.
  */
trait NamedActor {
  def name: String

  def poolName: String
}
