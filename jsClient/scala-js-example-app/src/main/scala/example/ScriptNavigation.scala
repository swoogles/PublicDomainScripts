package example

import org.scalajs.dom
import dom.document.getElementById
import org.scalajs.jquery.{JQuery, JQueryEventObject}
import zio.{IO, Task, ZIO}

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
    // Only setup controls if there is a character selected
    println("crudely retrieved character: " + targetCharacter)

    val targetCharacterLines: JQuery = jquery(s".$targetCharacter")

    if (dom.document.URL
          .contains(s"$TARGET_SCRIPT_VARIATION/")) { // What an ugly way to work with this for the time being
      ContentHiding.reveal(".two-row-layout")
    } else {
      ContentHiding.reveal(".one-row-layout")
    }

    val firstCharacterLine = targetCharacterLines.get(0)
    val currentTarget = new CurrentTarget(
      ConnectedLine(getElementById(firstCharacterLine.id))
    )

    ZIO {
      targetCharacterLines.each((index, line) => {
        jquery(line).click { eventObject: JQueryEventObject =>
          ContentHiding.toggleContent(eventObject)
          currentTarget.updateTarget(_ => line.id)
        }
        ContentHiding.showReducedContentOfJqueryElement(line)
        jquery(line).addClass("targetCharacter")
      })
    }.flatMap(
        _ =>
          ZIO {
            jquery(".scroll-to-next-line")
              .click { _: JQueryEventObject =>
                iterateToElement(
                  _.connectedLine.nextLineId,
                  1,
                  Next,
                  currentTarget
                )
              }
          }
      )
      .flatMap(
        _ =>
          ZIO {
            jquery(".scroll-to-next-line-big")
              .click { _: JQueryEventObject =>
                iterateToElement(
                  _.connectedLine.nextLineId,
                  10,
                  Next,
                  currentTarget
                )
              }
          }
      )
      .flatMap(
        _ =>
          ZIO {
            jquery(".scroll-to-previous-line")
              .click { _: JQueryEventObject =>
                iterateToElement(
                  _.connectedLine.previousLineId,
                  1,
                  Prev,
                  currentTarget
                )
              }
          }
      )
      .flatMap(
        _ =>
          ZIO {
            jquery(".scroll-to-previous-line-big")
              .click { _: JQueryEventObject =>
                iterateToElement(
                  _.connectedLine.previousLineId,
                  10,
                  Prev,
                  currentTarget
                )
              }
          }
      )

  }

  def setupScriptNavigationOrHideControls() = {
    println("1:47")

    val targetCharacterAttempt: Option[String] = getCurrentCharacter(
      dom.window.location.toString
    )
    ZIO.fromOption(targetCharacterAttempt).flatMap(setupForCharacter)
  }

}
