package example

import scala.scalajs.js

case class QueryParam(name: String, value: String)

object  QueryParam {
  def extractFromUrl(url: String): Seq[QueryParam] =
    url
      .dropWhile(_ != '?')
      .tail
      .split("&")
      .map(js.URIUtils.decodeURIComponent)
      .map(fullQueryParam => QueryParam(fullQueryParam.takeWhile(_ != '='), fullQueryParam.dropWhile(_ != '=').tail))
    .map{param => println("Extracted param: " + param); param}
}

