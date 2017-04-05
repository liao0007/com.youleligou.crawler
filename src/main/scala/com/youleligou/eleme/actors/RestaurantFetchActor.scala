package com.youleligou.eleme.actors

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.youleligou.crawler.actors.{AbstractFetchActor, CountActor, NamedActor}
import com.youleligou.crawler.services.FetchService

class RestaurantFetchActor @Inject()(config: Config,
                                     fetchService: FetchService,
                                     @Named(RestaurantParseActor.poolName) parserActor: ActorRef,
                                     @Named(CountActor.poolName) countActor: ActorRef)
  extends AbstractFetchActor(config, fetchService, parserActor, countActor)

object RestaurantFetchActor extends NamedActor {
  override final val name: String = "ElemeRestaurant" + "FetchActor"
  override final val poolName: String = "ElemeRestaurant" + "FetchActorPool"
}
