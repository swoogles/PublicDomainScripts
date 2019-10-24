package example

import scala.util.{Failure, Success}
import zio.{URIO, ZIO}
import org.scalajs.dom.Window
import org.scalajs.jquery.JQueryStatic

import org.scalajs.dom.html.Document

case class RangeInputFields(minimum: String, maximum: String)
class Button()

case class TrimRange(start: Int, end: Int) {
  assert(start < end)
}

object TrimRange {
  def safelyConstructed(start: Int, end: Int): Either[String, TrimRange] = {
    if (start > end)
      Left(s"start must be <= end. start=$start end=$end")
    else
      Right(TrimRange(start, end))
  }
}

case class Url(input: String)


object TrimValueFunctionality {

  def unimplementedStuff() = {
    val getTrimButtonsMonad: ZIO[JQueryStatic, Throwable, RangeInputFields] =
      ZIO.accessM { jquery =>
        ZIO {
          val trimStart = jquery("#trimStart").value().toString
          val trimEnd = jquery("#trimEnd").value().toString
          RangeInputFields(trimStart, trimEnd)
        }
      }

    val getCurrentUrl: URIO[Document, String] =
      URIO.accessM { document => URIO(document.URL) }

    val trimButtonsIntoValues: Function[RangeInputFields, TrimRange] =
      rangeInputFields => TrimRange(rangeInputFields.minimum.toInt, rangeInputFields.maximum.toInt)

    def applyTrimRangeToCurrentUrl: (TrimRange, Url) => Url = {
      throw new RuntimeException("We biffed it!")
    }

    val navigateToUrlInBrowser: ZIO[Window, Throwable, Unit] = ZIO.accessM {
      window =>
        ZIO {
          window.location.href = "someNewUrl"
        }
    }


    /*
    val browseToStripDownScriptSpecifiedByInputFields: ReadFromTheWorld[Document] => WriteToTheWorld[Document] =
      getTrimButtons
        .andThen(trimButtonsIntoValues)
        .andThen(applyTrimRangeToCurrentUrl)
        .andThen(navigateToUrlInBrowser)
     */


  }

  def indicesToKeep(trimValue: String) =
    ZIO.fromTry {
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

        case default => throw new RuntimeException("Dunno what happened here: " + default)
      }
    }

}
