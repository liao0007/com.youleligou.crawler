package com.youleligou.core.serializers

import com.youleligou.crawler.daos.{JobDao, ProxyServerDao}
import com.youleligou.eleme.daos._
import com.youleligou.meituan.daos._
import de.javakaffee.kryoserializers.jodatime.{JodaDateTimeSerializer, JodaLocalDateSerializer, JodaLocalDateTimeSerializer}
import org.joda.time.{DateTime, LocalDate, LocalDateTime}

/**
  * Created by liangliao on 2/5/17.
  */
class KryoRegistrator extends org.apache.spark.serializer.KryoRegistrator {
  override def registerClasses(kryo: com.esotericsoftware.kryo.Kryo): Unit = {
    /*
    register for eleme
     */
    kryo.register(classOf[CategoryDao])
    kryo.register(classOf[CategorySnapshotDao])
    kryo.register(classOf[CategoryDaoSearch])

    kryo.register(classOf[RestaurantDao])
    kryo.register(classOf[RestaurantSnapshotDao])
    kryo.register(classOf[RestaurantDaoSearch])
    kryo.register(classOf[RestaurantSnapshotDaoSearch])

    kryo.register(classOf[FoodSnapshotDao])
    kryo.register(classOf[FoodSnapshotDaoSearch])

    kryo.register(classOf[FoodSkuSnapshotDao])

    /*
    register for meituan
     */
    kryo.register(classOf[FoodTagDao])
    kryo.register(classOf[FoodTagSnapshotDao])
    kryo.register(classOf[FoodTagDaoSearch])

    kryo.register(classOf[PoiDao])
    kryo.register(classOf[PoiSnapshotDao])
    kryo.register(classOf[PoiDaoSearch])
    kryo.register(classOf[PoiSnapshotDaoSearch])

    kryo.register(classOf[SpuSnapshotDao])
    kryo.register(classOf[SpuSnapshotDaoSearch])

    kryo.register(classOf[SkuSnapshotDao])

    /*
    register for crawler system
     */
    kryo.register(classOf[JobDao])
    kryo.register(classOf[ProxyServerDao])

    kryo.register(classOf[DateTime], new JodaDateTimeSerializer)
    kryo.register(classOf[LocalDate], new JodaLocalDateSerializer)
    kryo.register(classOf[LocalDateTime], new JodaLocalDateTimeSerializer)
  }

}
