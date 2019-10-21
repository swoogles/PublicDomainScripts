package example

import scala.util.{Failure, Success, Try}
import zio.ZIO
import org.scalajs.dom
import org.scalajs.dom.Window
import org.scalajs.jquery.{JQuery, JQueryEventObject, JQueryStatic}
import zio.{Task, ZIO}
import zio.console._
import org.scalajs.dom.document.getElementById

import scala.scalajs.js
import org.scalajs.dom.html.Document

case class RangeInputFields(minimum: String, maximum: String)

object TrimValueFunctionality {

  val getTrimButtonsMonad: ZIO[JQueryStatic, Throwable, RangeInputFields] =
    ZIO.accessM{jquery => ZIO{
      val trimStart = jquery("#trimStart").value().toString
      val trimEnd = jquery("#trimEnd").value().toString
      RangeInputFields(trimStart, trimEnd)
    }}

  val getCurrentUrl: ZIO[Document, Throwable, String] =
    ZIO.accessM{document => ZIO(document.URL)}

  val trimButtonsIntoValues: Function[RangeInputFields, TrimRange] =
    rangeInputFields => TrimRange(rangeInputFields.minimum.toInt, rangeInputFields.maximum.toInt)

  val applyTrimRangeToCurrentUrl: (TrimRange, Url) => Url = {
    ???
  }
  val navigateToUrlInBrowser: ZIO[Window, Throwable, Unit] = ZIO.accessM{
    window=> ZIO{window.location.href = "someNewUrl"}
  }

  /*
  val browseToStripDownScriptSpecifiedByInputFields: ReadFromTheWorld[Document] => WriteToTheWorld[Document] =
    getTrimButtons
      .andThen(trimButtonsIntoValues)
      .andThen(applyTrimRangeToCurrentUrl)
      .andThen(navigateToUrlInBrowser)
   */

  val realProgramThatGetsTrimButtons =
    getTrimButtonsMonad

  trait Environment extends Window with JQueryStatic{
    val window: Window
    val jQueryStatic: JQueryStatic
  }

  def mainMethod () = {

    val theBigRealWindow: Window = org.scalajs.dom.window

    val cobbledTogetherEnvironment: Environment =
      new Environment {
        override val jQueryStatic: JQueryStatic = jquery
        override val window: Window = theBigRealWindow
      }

    val fullProgram: ZIO[Window with JQueryStatic, Throwable, Unit] =
    for {
      rangeInputFields <- getTrimButtonsMonad
      trimRange = trimButtonsIntoValues(rangeInputFields)
      newUrl = applyTrimRangeToCurrentUrl(trimRange, Url(theBigRealWindow.location.href))
      _ <- navigateToUrlInBrowser
    } yield {
      println("We done, broh!")
    }

    fullProgram.provide(cobbledTogetherEnvironment).run
//    fullProgram.provide(theBigRealWindow).provide(documentFromEnvironment)
//    browseToStripDownScriptSpecifiedByInputFields
//      .andThen(writeToTheWorld => writeToTheWorld.unsafeRun(documentFromEnvironment))
    /*
        val program: ZIO[Console, IOException, String] =
        for {
          _    <- println("Good morning, what is your name?")
          name <- readLine
          _    <- println(s"Good to meet you, $name!")
        } yield name

     */
  }

  class Button()
  case class TrimRange(start: Int, end: Int) {
    assert(start < end)
  }
  object TrimRange {
    def safelyConstructed(start: Int, end: Int): Either[String, TrimRange] = {
      if ( start > end )
        Left(s"start must be <= end. start=$start end=$end")
      else
        Right(TrimRange(start, end))
    }
  }
  class QueryParameter(name: String, value: String)
  case class Url(input: String)

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
