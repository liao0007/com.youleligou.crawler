package com.youleligou.eleme

import akka.actor.ActorSystem
import com.google.inject.Inject
import com.outworkers.phantom.dsl.DatabaseProvider
import com.typesafe.config.Config
import com.youleligou.eleme.daos.cassandra.{ElemeDatabase, Restaurant}
import com.youleligou.eleme.daos.mysql.RestaurantRepo
import org.joda.time.DateTime

/**
  * Created by liangliao on 18/4/17.
  */
class ElemeCassandraBootstrap @Inject()(config: Config, system: ActorSystem, val database: ElemeDatabase, restaurantRepo: RestaurantRepo)
    extends DatabaseProvider[ElemeDatabase] {
  import system.dispatcher

  def start(): Unit = {
    for {
      _           <- database.createAsync()
      restaurants <- restaurantRepo.all()

    } yield {
      restaurants foreach { restaurant =>
        database.restaurants
          .store(Restaurant(
            id = restaurant.id,
            address = restaurant.address,
            averageCost = restaurant.averageCost,
            description = restaurant.description,
            deliveryFee = restaurant.deliveryFee,
            minimumOrderAmount = restaurant.minimumOrderAmount,
            imagePath = restaurant.imagePath,
            isNew = restaurant.isNew,
            isPremium = restaurant.isPremium,
            latitude = restaurant.latitude,
            longitude = restaurant.longitude,
            name = restaurant.name,
            phone = restaurant.phone,
            promotionInfo = restaurant.promotionInfo,
            rating = restaurant.rating,
            ratingCount = restaurant.ratingCount,
            recentOrderNum = restaurant.recentOrderNum,
            licensesNumber = restaurant.licensesNumber,
            companyName = restaurant.companyName,
            status = restaurant.status
          ))
          .future()
      }
    }

  }
}

/*
class ElemeCassandraBootstrap @Inject()(config: Config, system: ActorSystem, val database: ElemeDatabase, foodRepo: FoodRepo)
    extends DatabaseProvider[ElemeDatabase] {
  import system.dispatcher

  def start(): Unit = {
    for {
      _     <- database.createAsync()
      foods <- foodRepo.all()

    } yield {
      foods foreach { food =>
        database.foods
          .store(Food(
            itemId = food.itemId,
            restaurantId = food.restaurantId,
            categoryId = food.categoryId,
            name = food.name,
            description = food.description,
            monthSales = food.monthSales,
            rating = food.rating,
            ratingCount = food.ratingCount,
            satisfyCount = food.satisfyCount,
            satisfyRate = food.satisfyRate,
            createdAt = DateTime.now()
          ))
          .future()
      }

    }
  }
}
 */
