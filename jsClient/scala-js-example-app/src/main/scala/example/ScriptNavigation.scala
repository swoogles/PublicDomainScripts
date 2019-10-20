package example

import org.scalajs.dom
import org.scalajs.jquery.{JQuery, JQueryEventObject}
import zio.{Task, ZIO}
import zio.console._
import org.scalajs.dom.document.getElementById

import scala.collection.immutable
import scala.scalajs.js
import scala.util.{Failure, Success, Try}

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
  def extractCurrentCharacterNameFromUrl(url: String): Option[String] = {
    QueryParam.extractFromUrl(url)
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

  def hideAllUnwantedScriptElements(indicesToKeep: Set[Int]): Task[String] =
    ZIO {
      jquery("[id^=script-element]").each((index: Int, line) =>
        if (!indicesToKeep.contains(index) ) ContentHiding.hideInstantly("#" + line.id)
      )
      "Yay, we trimmed."
    }

  def trimDownScriptIfQueryParameterIsPresent(url: String) =
    QueryParam
      .extractParameterValueFromUrl(url, "trim")
      .flatMap( trimValue =>
        TrimValueFunctionality.indicesToKeep(trimValue)
          .flatMap (hideAllUnwantedScriptElements)
      ).orElse( ZIO.succeed("No need to trim"))

  def getLinesForCharacter(targetCharacter: String) = ZIO {
    jquery(s".$targetCharacter")
  }

  val showCorrectControls = ZIO {
    if (dom.document.URL
      .contains(s"$TARGET_SCRIPT_VARIATION/")) { // What an ugly way to work with this for the time being
      ContentHiding.reveal(".two-row-layout")
    } else {
      ContentHiding.reveal(".one-row-layout")
    }
  }

  object ScrollButtonBehavior {
    def attachNextLineBehavior(currentTarget: CurrentTarget) =
      attachClickBehaviorToElement(
      ".scroll-to-next-line",
      _ =>
        iterateToElement(
          _.connectedLine.nextLineId,
          1,
          Next,
          currentTarget
        )
    )

    def attachBigNextLineBehavior(currentTarget: CurrentTarget) =
      attachClickBehaviorToElement(
      ".scroll-to-next-line-big",
      _ =>
        iterateToElement(
          _.connectedLine.nextLineId,
          10,
          Next,
          currentTarget
        )
    )

    def attachPreviousLineBehavior(currentTarget: CurrentTarget) = attachClickBehaviorToElement(
      ".scroll-to-previous-line",
      _ =>
        iterateToElement(
          _.connectedLine.previousLineId,
          1,
          Prev,
          currentTarget
        )
    )

    def attachBigPreviousLineBehavior(currentTarget: CurrentTarget) = attachClickBehaviorToElement(
      ".scroll-to-previous-line-big",
      _ =>
        iterateToElement(
          _.connectedLine.previousLineId,
          10,
          Prev,
          currentTarget
        )
    )

    val allButtonBehaviors = (currentTargetLocal: CurrentTarget) =>
      ZIO.collectAll(
        List(
          attachNextLineBehavior(currentTargetLocal),
          attachBigNextLineBehavior(currentTargetLocal),
          attachPreviousLineBehavior(currentTargetLocal),
          attachBigPreviousLineBehavior(currentTargetLocal)
        )
      )
  }

  def setupCharacterLineInitialStateAndBehavior(targetCharacterLines: JQuery, currentTargetLocal: CurrentTarget) =
    ZIO {
      targetCharacterLines.each((line: dom.Element) => {
        jquery(line).click { eventObject: JQueryEventObject =>
          ContentHiding.toggleContent(eventObject)
          currentTargetLocal.updateTarget(_ => line.id)
        }
        ContentHiding.showReducedContentOfJqueryElement(line)
        jquery(line).addClass("targetCharacter")
      })
    }



  def setupForCharacter(targetCharacter: String) = {
    getLinesForCharacter(targetCharacter).flatMap { targetCharacterLines: JQuery =>

      val currentTargetLocal = new CurrentTarget(
        ConnectedLine(getElementById(targetCharacterLines.get(0).id))
      )

        putStrLn("Number of lines: " + targetCharacterLines.length)
        .flatMap(_ => setupCharacterLineInitialStateAndBehavior(targetCharacterLines, currentTargetLocal))
        .flatMap(_ => showCorrectControls)
        .flatMap(_ => ScrollButtonBehavior.allButtonBehaviors(currentTargetLocal))

    }
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
            // Only setup controls if there is a character selected
            .flatMap(setupForCharacter)
            .mapError(_ => "No character found")
      )
//      .flatMap(trimDownScript)
  }

}
