package example

import org.scalajs.dom
import dom.document.getElementById
import org.scalajs.jquery.{JQuery, JQueryEventObject}
import zio.{IO, Task, ZIO}
import zio.console.putStrLn

import scala.collection.immutable
import scala.scalajs.js

object ScriptNavigation {
  val TARGET_SCRIPT_VARIATION =
//    "full_script_with_lines_highlighted"
    "completely_blank_lines_with_spoken_cues"

  private def iterateToElement(
      targetId: CurrentTarget => String,
      numSteps: Int,
      scrollingTarget: ScrollingTarget,
      currentTarget: CurrentTarget
  ): Unit = {
    currentTarget.updateTarget(targetId)

    if (numSteps > 1)
      iterateToElement(targetId, numSteps - 1, scrollingTarget, currentTarget)
    else {
      if (dom.document.URL.contains(s"$TARGET_SCRIPT_VARIATION/")) { // What an ugly way to work with this for the time being
        currentTarget.connectedLine.cueLine.scrollIntoView(true)
        SpeechClient.speak(currentTarget.connectedLine.cueLineContent)
      } else {
        currentTarget.connectedLine.dataScrollBuffer.scrollIntoView(false)
      }
    }
  }

  // TODO The next 2 bits belong elsewhere
  case class QueryParam(name: String, value: String)

  private def extractQueryParams(url: String): Seq[QueryParam] =
    url
      .dropWhile(_ != '?')
      .split("&")
      .map(js.URIUtils.decodeURIComponent)
    .map(fullQueryParam => QueryParam(fullQueryParam.takeWhile(_ != '='), fullQueryParam.dropWhile(_ != '=').tail))

  def extractCurrentCharacterNameFromUrl(url: String): Option[String] = {
    extractQueryParams(url)
      .filter(_.name == "character")
      .map(_.value)
      .headOption
  }

  def getCurrentCharacter(url: String): Option[String] = {
    extractCurrentCharacterNameFromUrl(url).filter { targetCharacter =>
      val targetCharacterLines: JQuery = jquery(s".$targetCharacter")

      targetCharacterLines.length > 0 // There are no characters, so we're not viewing a script that needs controls.
    }
  }

  def trimDownScript(url: String) = {
    if(url.contains("TRIM")) {
      val desiredLineRange = (0, 10)
      val desiredLineIndices: immutable.Seq[Int] = (desiredLineRange._1 to desiredLineRange._2)
      val indexMap = desiredLineIndices.foldLeft(Map[Int, Boolean]()) { (map, index) => {
        map + (index -> true)
      }
      }

      putStrLn("trimming down script")
        .flatMap(_ =>
          ZIO {
            jquery("[id^=script-element]").each((index, line) => {
              if (!indexMap.getOrElse(index, false))
              //      if (! desiredLineIndices.exists( index => line.id == s"script-element-$index"))
                ContentHiding.hideInstantly("#" + line.id)
              println("Line: " + line.id)
            }

            )
          }
        )

    } else {
      ZIO { "No need to trim"}
    }
  }

  def setupForCharacter(targetCharacter: String) = {
    val targetCharacterLines: JQuery = jquery(s".$targetCharacter")


    val showCorrectControls = ZIO {
      if (dom.document.URL
            .contains(s"$TARGET_SCRIPT_VARIATION/")) { // What an ugly way to work with this for the time being
        ContentHiding.reveal(".two-row-layout")
      } else {
        ContentHiding.reveal(".one-row-layout")
      }
    }

    val firstCharacterLine = targetCharacterLines.get(0)
    val currentTarget = new CurrentTarget(
      ConnectedLine(getElementById(firstCharacterLine.id))
    )

    val attachNextLineBehavior = attachClickBehaviorToElement(
      ".scroll-to-next-line",
      _ =>
        iterateToElement(
          _.connectedLine.nextLineId,
          1,
          Next,
          currentTarget
        )
    )

    val attachBigNextLineBehavior = attachClickBehaviorToElement(
      ".scroll-to-next-line-big",
      _ =>
        iterateToElement(
          _.connectedLine.nextLineId,
          10,
          Next,
          currentTarget
        )
    )

    val attachPreviousLineBehavior = attachClickBehaviorToElement(
      ".scroll-to-previous-line",
      _ =>
        iterateToElement(
          _.connectedLine.previousLineId,
          1,
          Prev,
          currentTarget
        )
    )

    val attachBigPreviousLineBehavior = attachClickBehaviorToElement(
      ".scroll-to-previous-line-big",
      _ =>
        iterateToElement(
          _.connectedLine.previousLineId,
          10,
          Prev,
          currentTarget
        )
    )



    val setupCharacterLineInitialStateAndBehavior =
      ZIO {
        targetCharacterLines.each((index, line) => {
          jquery(line).click { eventObject: JQueryEventObject =>
            ContentHiding.toggleContent(eventObject)
            currentTarget.updateTarget(_ => line.id)
          }
          ContentHiding.showReducedContentOfJqueryElement(line)
          jquery(line).addClass("targetCharacter")
        })
      }

    putStrLn("decently retrieved character: " + targetCharacter)
        .flatMap( _ => putStrLn("Number of lines: " + targetCharacterLines.length))
      .flatMap(_ => setupCharacterLineInitialStateAndBehavior)
      .flatMap(_ => showCorrectControls)
      .flatMap(_ => attachNextLineBehavior)
      .flatMap(_ => attachBigNextLineBehavior)
      .flatMap(_ => attachPreviousLineBehavior)
      .flatMap(_ => attachBigPreviousLineBehavior)

  }

  def attachClickBehaviorToElement(
      selector: String,
      func: JQueryEventObject => js.Any
  ) = ZIO {
    jquery(selector)
      .click { func }
  }

  def setupScriptNavigationOrHideControls() = {
    val targetCharacterAttempt: Option[String] = getCurrentCharacter(
      dom.window.location.toString
    )
    putStrLn("8:15")
      .flatMap(
        _ =>
          ZIO
            .fromOption(targetCharacterAttempt)
            .mapError(_ => "No character found")
            // Only setup controls if there is a character selected
            .flatMap(setupForCharacter)
      )
//      .flatMap(trimDownScript)
  }

}
