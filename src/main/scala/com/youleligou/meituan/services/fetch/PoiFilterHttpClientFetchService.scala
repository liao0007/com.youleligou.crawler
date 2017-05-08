package com.youleligou.meituan.services.fetch

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.core.reps.Repo
import com.youleligou.crawler.daos.JobDao
import com.youleligou.crawler.models.{FetchRequest, FetchResponse, UrlInfo}
import com.youleligou.crawler.services.FetchService
import com.youleligou.crawler.services.fetch.HttpClientFetchService
import org.joda.time.DateTime
import play.api.libs.json.{JsString, Json}
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.libs.ws.{StandaloneWSRequest, StandaloneWSResponse}

import scala.concurrent.{ExecutionContext, Future}

class PoiFilterHttpClientFetchService @Inject()(config: Config, jobRepo: Repo[JobDao], standaloneAhcWSClient: StandaloneAhcWSClient)
    extends HttpClientFetchService(config, jobRepo, standaloneAhcWSClient)
    with Deflatable {

  override def fetch(fetchRequest: FetchRequest)(implicit executor: ExecutionContext): Future[FetchResponse] = {
    val urlInfo: UrlInfo                     = fetchRequest.urlInfo
    val latitude: Float                      = urlInfo.queryParameters("lat").toFloat
    val longitude: Float                     = urlInfo.queryParameters("lng").toFloat
    val paginationSeq: Seq[(String, String)] = urlInfo.bodyParameters.toSeq

    val locationSeq = Seq(
      "lat" -> latitude.toString,
      "lng" -> longitude.toString
    )

    //calculate token
    val parameter = locationSeq ++ paginationSeq
    val sign      = deflate(parameter.sortBy(_._1).toMap)
    val timestamp = DateTime.now().getMillis.toString
    val token = deflate(
      Map(
        "sign" -> sign,
        "cts"  -> timestamp
      )
    )

    val getParameter: Seq[(String, String)] = locationSeq :+ ("_token", token)
    val url: String = urlInfo.domain + urlInfo.path + getParameter
      .map { case (key, value) => s"$key=$value" }
      .mkString("&")
    val body = urlInfo.bodyParameters
      .map { case (key, value) => s"$key=$value" }
      .mkString("&")
    val cookie = s"w_latlng=${Math.round(latitude * 1000000)},${Math.round(longitude * 1000000)};"

    val request: StandaloneWSRequest = buildRequest(
      url,
      Seq(
        "Host"            -> "i.waimai.meituan.com",
        "Accept"          -> "application/json",
        "Content-Type"    -> "application/x-www-form-urlencoded",
        "Origin"          -> "http://i.waimai.meituan.com",
        "Connection"      -> "keep-alive",
        "Accept-Language" -> "zh-CN",
        "Accept-Encoding" -> "gzip, deflate",
        "Cookie"          -> cookie
      )
    )
    val response: Future[StandaloneWSResponse] = makeRequest(request) { r =>
      r.post(body)
    }
    processResponse(fetchRequest, response) map { fetchResponse =>
      if ((Json.parse(fetchResponse.content) \ "msg" toOption).contains(JsString("成功")))
        fetchResponse
      else {
        fetchResponse.copy(status = FetchService.Misc)
      }
    }

  }

}
