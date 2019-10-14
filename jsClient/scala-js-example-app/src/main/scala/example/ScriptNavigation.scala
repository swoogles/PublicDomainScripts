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

  def extractCurrentCharacterNameFromUrl(url: String): Option[String] = {
    // TODO Use a real, typed URL value
    // val fields=temp_url.split("&").map(js.URIUtils.decodeURIComponent)
    val targetCharacterWithPrefix = url.dropWhile(_ != '=')

    if (!targetCharacterWithPrefix.isEmpty)
      Some(targetCharacterWithPrefix.tail)
    else
      None
  }

  def getCurrentCharacter(url: String): Option[String] = {
    extractCurrentCharacterNameFromUrl(url).filter { targetCharacter =>
      val targetCharacterLines: JQuery = jquery(s".$targetCharacter")

      targetCharacterLines.length > 0 // There are no characters, so we're not viewing a script that needs controls.
    }
  }

  def setupForCharacter(targetCharacter: String) = {
    val desiredLineRange = (50, 100)
    val desiredLineIndices: immutable.Seq[Int] = (desiredLineRange._1 to desiredLineRange._2)

    jquery("[id^=script-element]").each((index, line) => {
      if (! desiredLineIndices.exists( index => line.id == s"script-element-$index"))
        ContentHiding.hideInstantly("#" + line.id)
      println("Line: " + line.id)
    }

    )
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

    putStrLn("crudely retrieved character: " + targetCharacter)
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
  }

}
