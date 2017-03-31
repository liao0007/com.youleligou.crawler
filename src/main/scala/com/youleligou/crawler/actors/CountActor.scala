package com.youleligou.crawler.actors

import akka.actor.Actor
import com.youleligou.crawler.actors.CountActor._

/**
  * Created by young.yang on 2016/9/3.
  * 用来对任务进行计数
  */
class CountActor extends Actor {

  private var fetchCounter = FetchCounter(0)
  private var fetchOk = FetchOk(0)
  private var fetchError = FetchError(0)
  private var injectCounter = InjectCounter(0)
  private var parseCounter = ParseCounter(0)
  private var parseChildUrlCounter = ParseChildUrlCounter(0)
  private var indexCounter = IndexCounter(0)

  private def printCounter(): String = {
    val buffer = new StringBuilder
    buffer.append("task counter details start ------" + "\n")
    buffer.append("fetchCounter = [" + fetchCounter.num + "]" + "\n")
    buffer.append("fetchOk = [" + fetchOk.num + "]" + "\n")
    buffer.append("fetchError = [" + fetchError.num + "]" + "\n")
    buffer.append("injectCounter = [" + injectCounter.num + "]" + "\n")
    buffer.append("parseCounter = [" + parseCounter.num + "]" + "\n")
    buffer.append("parseChildUrlCounter = [" + parseChildUrlCounter.num + "]" + "\n")
    buffer.append("indexCounter = [" + indexCounter.num + "]" + "\n")
    buffer.append("task counter details end -------")
    buffer.toString()
  }

  private def getAllCounter: AllCounter =
    AllCounter(fetchCounter, fetchOk, fetchError, injectCounter, parseCounter, parseChildUrlCounter, indexCounter)

  override def receive: Receive = {
    case counter: FetchCounter => fetchCounter = FetchCounter(fetchCounter.num + counter.num)
    case count: FetchOk => fetchOk = FetchOk(count.num + fetchOk.num)
    case count: FetchError => fetchError = FetchError(count.num + fetchError.num)
    case count: InjectCounter => injectCounter = InjectCounter(count.num + injectCounter.num)
    case count: ParseCounter => parseCounter = ParseCounter(count.num + parseCounter.num)
    case count: ParseChildUrlCounter => parseChildUrlCounter = ParseChildUrlCounter(count.num + parseChildUrlCounter.num)
    case count: IndexCounter => indexCounter = IndexCounter(count.num + indexCounter.num)
    case PrintCounter => sender() ! printCounter()
    case GetAllCounter => sender() ! getAllCounter
  }
}

object CountActor extends NamedActor {
  override final val name = "CountActor"

  sealed trait Counter

  case class FetchCounter(num: Int) extends Counter

  case class FetchOk(num: Int) extends Counter

  case class FetchError(num: Int) extends Counter

  case class InjectCounter(num: Int) extends Counter

  case class ParseCounter(num: Int) extends Counter

  case class ParseChildUrlCounter(num: Int) extends Counter

  case class IndexCounter(num: Int) extends Counter

  case object PrintCounter extends Counter

  case object GetAllCounter extends Counter

  case class AllCounter(fetchCounter: FetchCounter,
                        fetchOk: FetchOk,
                        fetchError: FetchError,
                        injectCounter: InjectCounter,
                        parseCounter: ParseCounter,
                        parseChildUrlCounter: ParseChildUrlCounter,
                        indexCounter: IndexCounter)
    extends Counter

}
