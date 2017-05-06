package com.youleligou.core.serializers

import com.youleligou.crawler.daos.{JobDao, ProxyServerDao}
import com.youleligou.eleme.daos.{FoodSnapshotDao, RestaurantDao}
import de.javakaffee.kryoserializers.jodatime.{JodaDateTimeSerializer, JodaLocalDateSerializer, JodaLocalDateTimeSerializer}
import org.joda.time.{DateTime, LocalDate, LocalDateTime}

/**
  * Created by liangliao on 2/5/17.
  */
class KryoRegistrator extends org.apache.spark.serializer.KryoRegistrator {
  override def registerClasses(kryo: com.esotericsoftware.kryo.Kryo): Unit = {
    kryo.register(classOf[RestaurantDao])
    kryo.register(classOf[FoodSnapshotDao])
    kryo.register(classOf[RestaurantDao])
    kryo.register(classOf[JobDao])
    kryo.register(classOf[ProxyServerDao])

    kryo.register(classOf[DateTime], new JodaDateTimeSerializer)
    kryo.register(classOf[LocalDate], new JodaLocalDateSerializer)
    kryo.register(classOf[LocalDateTime], new JodaLocalDateTimeSerializer)
  }

}
