package example

import org.scalajs.dom
import dom.document.getElementById
import org.scalajs.jquery.{JQuery, JQueryEventObject}

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

  def getCurrentCharacterName(url: String): Option[String] = {
    // TODO Use a real, typed URL value
    // val fields=temp_url.split("&").map(js.URIUtils.decodeURIComponent)
    val targetCharacterWithPrefix = url.dropWhile(_ != '=')

    if (!targetCharacterWithPrefix.isEmpty)
      Some(targetCharacterWithPrefix.tail)
    else
      None
  }

  def setupScriptNavigationOrHideControls() {
    println("9:59")

    val targetCharacterAttempt: Option[String] = getCurrentCharacterName(
      dom.window.location.toString
    )
    targetCharacterAttempt.foreach(
      targetCharacter => { // Only setup controls if there is a character selected
        println("crudely retrieved character: " + targetCharacter)

        val targetCharacterLines: JQuery = jquery(s".$targetCharacter")

        if (targetCharacterLines.length > 0) { // There are no characters, so we're not viewing a script that needs controls.
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

          targetCharacterLines.each((index, line) => {
            jquery(line).click{eventObject: JQueryEventObject =>
              ContentHiding.toggleContent(eventObject)
              currentTarget.updateTarget(_ => line.id)
            }
            jquery(line).addClass("targetCharacter")
          })

          jquery(".scroll-to-next-line")
            .click { _: JQueryEventObject =>
              iterateToElement(
                _.connectedLine.nextLineId,
                1,
                Next,
                currentTarget
              )
            }

          jquery(".scroll-to-next-line-big")
            .click { _: JQueryEventObject =>
              iterateToElement(
                _.connectedLine.nextLineId,
                10,
                Next,
                currentTarget
              )
            }

          jquery(".scroll-to-previous-line")
            .click { _: JQueryEventObject =>
              iterateToElement(
                _.connectedLine.previousLineId,
                1,
                Prev,
                currentTarget
              )
            }

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

      }
    )
  }

}
