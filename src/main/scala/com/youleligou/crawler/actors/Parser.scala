package com.youleligou.crawler.actors

import javax.inject.Named

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.name.Names
import com.google.inject.{Inject, Key}
import com.typesafe.config.Config
import com.youleligou.crawler.models.{FetchResponse, ParseResult}
import com.youleligou.crawler.modules.GuiceAkkaActorRefProvider
import com.youleligou.crawler.services.{HasService, ParseService}

/**
  * Created by young.yang on 2016/8/28.
  * 解析任务
  */
class Parser @Inject()(config: Config, injector: com.google.inject.Injector, @Named(Injector.PoolName) injectors: ActorRef)
    extends Actor
    with GuiceAkkaActorRefProvider
    with ActorLogging {

  val indexer: ActorRef = provideActorRef(context.system, Indexer, Some(context))

  override def receive: Receive = {
    case Parser.Parse(fetchResponse) =>
      log.debug("{} parse {}", self.path, fetchResponse.fetchRequest.urlInfo)

      fetchResponse.fetchRequest.urlInfo.services.get(Parser.ServiceNameKey).map { serviceName =>
        val parseService             = injector.getInstance(Key.get(classOf[ParseService], Names.named(serviceName)))
        val parseResult: ParseResult = parseService.parse(fetchResponse)
        parseResult.childLink.foreach { urlInfo =>
          injectors ! Injector.Inject(parseResult.fetchResponse.fetchRequest.copy(urlInfo = urlInfo, retry = 0))
        }
      }
  }
}

object Parser extends NamedActor with HasService {
  final val Name           = "ParseActor"
  final val PoolName       = "ParseActorPool"
  final val ServiceNameKey = "ParseService"

  sealed trait Command
  sealed trait Event

  case class Parse(fetchResponse: FetchResponse) extends Command
}
