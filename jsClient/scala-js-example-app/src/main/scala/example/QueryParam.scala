package example

import zio.ZIO

import scala.scalajs.js

case class QueryParam(name: String, value: String)

object  QueryParam {
  def extractFromUrl(url: String): Seq[QueryParam] =
    if (url.contains('?'))
    url
      .dropWhile(_ != '?')
      .tail
      .split("&")
      .map(js.URIUtils.decodeURIComponent)
      .map(fullQueryParam =>
        QueryParam(fullQueryParam.takeWhile(_ != '='), fullQueryParam.dropWhile(_ != '=').tail)
      )
    else
    Seq()


  def extractParameterValueFromUrl(url: String, parameterName: String) =
    ZIO.fromOption(
      QueryParam.extractFromUrl(url)
        .filter(_.name == parameterName)
        .map(_.value)
        .headOption
    )}

