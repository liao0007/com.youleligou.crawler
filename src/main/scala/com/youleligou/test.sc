val domain = "http://i.waimai.meituan.com"
val path = "/ajax/v6/poi/filter"
val queryParameters = Map(
  "lat" -> 31.23,
  "lng" -> 121.47
)



val url: String = {
  val parameterString =
    if (queryParameters.nonEmpty)
      queryParameters
        .map(parameter => parameter._1 + "=" + parameter._2)
        .mkString(if (path.contains("?")) "&" else "?", "&", "")
    else ""
  domain + path + parameterString
}