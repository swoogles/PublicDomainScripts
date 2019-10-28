package example

import scala.util.{Failure, Success}
import zio.{Task, URIO, ZIO}
import org.scalajs.dom.Window
import org.scalajs.jquery.JQueryStatic
import org.scalajs.dom.html.Document

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

  def multipleIndexRangesToKeep(trimValue: String) = {

    import java.util
    import java.util.regex.Pattern
    val matchList = new util.ArrayList[zio.Task[Set[Int]]]
    val regex = Pattern.compile("\\[(.*?)\\]")
    val regexMatcher = regex.matcher(trimValue)

    while ( regexMatcher.find ) { //Finds Matching Pattern in String
      matchList.add(
        ZIO.fromTry {
          println("Attempting to split : " + regexMatcher.group(1))
          regexMatcher.group(1).span(_ != ',') match {
            case (rangeStart: String, rangeEnd: String) => {
              try {
                Success {
                  Range.inclusive(rangeStart.toInt, rangeEnd.tail.toInt)
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
      ) //Fetching Group from String
      println("Matched group: " + regexMatcher.group(1))
    }

    import scala.collection.JavaConversions._

    val startingZio: zio.Task[Set[Int]] = ZIO{Set[Int]()}
    matchList.foldLeft(startingZio)( (acc: Task[Set[Int]], task: Task[Set[Int]]) => acc.*>(task))

  }

  def indicesToKeep(trimValue: String) =
    ZIO.fromTry {
      trimValue.span(_ != ',') match {
        case (rangeStart: String, rangeEnd: String) => {
          try {
            Success {
              Range.inclusive(rangeStart.toInt, rangeEnd.tail.toInt)
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
