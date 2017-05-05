package com.youleligou.core.serializers

import com.youleligou.crawler.daos.{JobDao, ProxyServerDao}
import com.youleligou.eleme.daos.accumulate.RestaurantAccumulate
import com.youleligou.eleme.daos.snapshot.{FoodSnapshot, RestaurantSnapshot}
import de.javakaffee.kryoserializers.jodatime.{JodaDateTimeSerializer, JodaLocalDateSerializer, JodaLocalDateTimeSerializer}
import org.joda.time.{DateTime, LocalDate, LocalDateTime}

/**
  * Created by liangliao on 2/5/17.
  */
class KryoRegistrator extends org.apache.spark.serializer.KryoRegistrator {
  override def registerClasses(kryo: com.esotericsoftware.kryo.Kryo): Unit = {
    kryo.register(classOf[RestaurantAccumulate])
    kryo.register(classOf[FoodSnapshot])
    kryo.register(classOf[RestaurantAccumulate])
    kryo.register(classOf[JobDao])
    kryo.register(classOf[ProxyServerDao])

    kryo.register(classOf[DateTime], new JodaDateTimeSerializer)
    kryo.register(classOf[LocalDate], new JodaLocalDateSerializer)
    kryo.register(classOf[LocalDateTime], new JodaLocalDateTimeSerializer)
  }

}
