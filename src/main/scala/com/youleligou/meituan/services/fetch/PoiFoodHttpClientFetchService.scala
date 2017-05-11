package com.youleligou.meituan.services.fetch

import com.google.inject.Inject
import com.typesafe.config.Config
import com.youleligou.core.reps.{ElasticSearchRepo, Repo}
import com.youleligou.crawler.daos.{JobDao, JobDaoSearch}
import com.youleligou.crawler.models.{FetchRequest, FetchResponse, UrlInfo}
import com.youleligou.crawler.services.FetchService
import com.youleligou.crawler.services.fetch.HttpClientFetchService
import org.joda.time.DateTime
import play.api.libs.json.{JsString, Json}
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.libs.ws.{StandaloneWSRequest, StandaloneWSResponse}

import scala.concurrent.{ExecutionContext, Future}

class PoiFoodHttpClientFetchService @Inject()(config: Config, jobSearchRepo: Repo[JobDaoSearch], standaloneAhcWSClient: StandaloneAhcWSClient)
    extends HttpClientFetchService(config, jobSearchRepo, standaloneAhcWSClient)
    with Deflatable {

  override def fetch(fetchRequest: FetchRequest)(implicit executor: ExecutionContext): Future[FetchResponse] = {
    val urlInfo: UrlInfo = fetchRequest.urlInfo
    val wmPoiId: String  = urlInfo.bodyParameters("wm_poi_id")

    //calculate token
    val parameters = Seq("wm_poi_id" -> wmPoiId)
    val sign       = deflate(parameters.sortBy(_._1).toMap)
    val timestamp  = DateTime.now().getMillis.toString
    val token = deflate(
      Map(
        "sign" -> sign,
        "cts"  -> timestamp
      )
    )

    val getParameter: Seq[(String, String)] = Seq("_token" -> token)
    val url: String = urlInfo.domain + urlInfo.path + getParameter
      .map { case (key, value) => s"$key=$value" }
      .mkString("&")
    val body = urlInfo.bodyParameters
      .map { case (key, value) => s"$key=$value" }
      .mkString("&")

    val request: StandaloneWSRequest = buildRequest(
      url,
      Seq(
        "Host"             -> "i.waimai.meituan.com",
        "Accept"           -> "application/json",
        "Referer"          -> s"http://i.waimai.meituan.com/restaurant/$wmPoiId",
        "X-Requested-With" -> "XMLHttpRequest",
        "Content-Type"     -> "application/x-www-form-urlencoded",
        "Origin"           -> "http://i.waimai.meituan.com"
      )
    )
    val response: Future[StandaloneWSResponse] = makeRequest(request) { r =>
      r.post(body)
    }
    processResponse(fetchRequest, response) map { fetchResponse =>
      if (fetchResponse.status == FetchService.Ok) {
        if ((Json.parse(fetchResponse.content) \ "msg" toOption).contains(JsString("成功")))
          fetchResponse
        else {
          fetchResponse.copy(status = FetchService.Misc)
        }
      } else {
        fetchResponse
      }

    }
  }

}
