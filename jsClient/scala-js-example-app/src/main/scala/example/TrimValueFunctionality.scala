package example

import scala.util.{Failure, Success, Try}
import zio.ZIO

import org.scalajs.dom
import org.scalajs.jquery.{JQuery, JQueryEventObject}
import zio.{Task, ZIO}
import zio.console._
import org.scalajs.dom.document.getElementById

import scala.scalajs.js
import org.scalajs.dom.html.Document


object TrimValueFunctionality {
  val getTrimButtons: Function[ReadFromTheWorld[Document], List[Button]] = ???
  val getTrimButtonsMonad: ZIO[Document, Throwable, String] =
    ZIO.accessM{document => ZIO(document.URL)}

  val trimButtonsIntoValues: Function[List[Button], TrimRange] = ???
  val applyTrimRangeToCurrentUrl: Function[TrimRange, Url] = ???
  val navigateToUrlInBrowser: Function[Url, WriteToTheWorld[Document]] = ???
  val navigateToUrlInBrowserMonad: WriteToTheWorld[Document] = ???

  val browseToStripDownScriptSpecifiedByInputFields: ReadFromTheWorld[Document] => WriteToTheWorld[Document] =
    getTrimButtons
      .andThen(trimButtonsIntoValues)
      .andThen(applyTrimRangeToCurrentUrl)
      .andThen(navigateToUrlInBrowser)

  val realProgramThatGetsTrimButtons =
    getTrimButtonsMonad

//  val roundTripProgram =
//    realProgramThatGetsTrimButtons
//      .flatMap( input => navigateToUrlInBrowserMonad)


  def mainMethod () = {
    val documentFromEnvironment: Document = ???
    browseToStripDownScriptSpecifiedByInputFields
      .andThen(writeToTheWorld => writeToTheWorld.unsafeRun(documentFromEnvironment))
  }

  class Button()
  class TrimRange(start: Int, end: Int)
  class QueryParameter(name: String, value: String)
  class Url()

  def createUrlWithNewTrimRange(baseUrl: String): Url = ???

  def indicesToKeep(trimValue: String) = ZIO.fromTry(
    //    val (rangeStartString: String, rangeEndString: String) =
    trimValue.span(_ != ',') match {
      case (rangeStart: String, rangeEnd: String) => {
        try {
          Success {
            Range(rangeStart.toInt, rangeEnd.tail.toInt)
              .foldLeft(Set[Int]()) {
                (map, index) => map + index
              }
          }
        } catch {
          case badValue: NumberFormatException => Failure(new RuntimeException("Cannot get indices from value: " + badValue))
        }
      }

    }
  )

}
