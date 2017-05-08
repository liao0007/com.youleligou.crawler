package com.youleligou.crawler.models

import com.youleligou.crawler.models.UrlInfo.UrlInfoType
import play.api.libs.json.Json

/**
  * 爬取url类
  *
  * @param domain domain
  */
case class UrlInfo(domain: String,
                   path: String,
                   queryParameters: Map[String, String] = Map.empty[String, String],
                   bodyParameters: Map[String, String] = Map.empty[String, String],
                   urlType: String = UrlInfoType.Generated,
                   jobType: String,
                   deep: Int = 0,
                   services: Map[String, String] = Map.empty[String, String]) {
  val url: String = {
    val parameterString =
      if (queryParameters.nonEmpty)
        queryParameters
          .map(parameter => parameter._1 + "=" + parameter._2)
          .mkString(if (path.contains("?")) "&" else "?", "&", "")
      else ""
    domain + path + parameterString
  }

  override def toString: String =
    url + "/" + bodyParameters
      .map(parameter => parameter._1 + "=" + parameter._2)
      .mkString("&")

  def withPath(newPath: String): UrlInfo = {
    val trimmedPath = newPath.trim
    copy(path = if (trimmedPath.startsWith("/")) trimmedPath else (path.split("/").dropRight(1).toSeq ++ Seq(trimmedPath)).mkString("/"))
  }

}

object UrlInfo {

  implicit val format = Json.format[UrlInfo]

  sealed trait UrlType
  object UrlInfoType {
    case object Seed      extends UrlType
    case object Generated extends UrlType
    implicit def fromString(x: String): Option[UrlType] = Seq(Seed, Generated).find(_.toString == x)
    implicit def toString(x: UrlType): String           = x.toString
  }

}
