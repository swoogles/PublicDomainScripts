package example

import org.scalajs.dom
import dom.document.getElementById
import org.scalajs.jquery.{JQuery, JQueryEventObject}

class CurrentTargets(
               var previousLineId: String,
               var nextLineId: String
             )

object ScriptNavigation {
  var targetCharacterLines: JQuery = _

  val TARGET_SCRIPT_VARIATION =
//    "full_script_with_lines_highlighted"
  "completely_blank_lines_with_spoken_cues"

  private def iterateToElement(targetId: (CurrentTargets) => String, numSteps: Int, scrollingTarget: ScrollingTarget, currentTargets: CurrentTargets): Unit = {
    val targetLine = getElementById(targetId(currentTargets))
    val targetLineTyped = ConnectedLine(targetLine)

    currentTargets.nextLineId = targetLineTyped.nextLineId
    currentTargets.previousLineId = targetLineTyped.previousLineId
    if( numSteps > 0)
      iterateToElement(targetId, numSteps -1, scrollingTarget, currentTargets)
    else
      scrollToElementWithBuffer(targetId, scrollingTarget, currentTargets)
  }

  private def scrollToElementWithBuffer(targetId: (CurrentTargets) => String, scrollingTarget: ScrollingTarget, currentTargets: CurrentTargets): Unit = {
    val targetLine = getElementById(targetId(currentTargets))
    val targetLineTyped = ConnectedLine(targetLine)

    currentTargets.nextLineId = targetLineTyped.nextLineId
    currentTargets.previousLineId = targetLineTyped.previousLineId

    if(dom.document.URL.contains(s"$TARGET_SCRIPT_VARIATION/")) { // What an ugly way to work with this for the time being
      targetLineTyped.cueLine.scrollIntoView(true)

      SpeechClient.speak(targetLineTyped.cueLineContent)
    } else {
      targetLineTyped.dataScrollBuffer.scrollIntoView(false)
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
    println("3:36")

    val targetCharacterAttempt: Option[String] = getCurrentCharacterName(dom.window.location.toString)
    targetCharacterAttempt.foreach( targetCharacter => { // Only setup controls if there is a character selected
      println("crudely retrieved character: " + targetCharacter)

      val targetCharacterLines = jquery(s".$targetCharacter")

      if (targetCharacterLines.length == 0) { // There are no characters, so we're not viewing a script that needs controls.
        ContentHiding.hideInstantly(".one-row-layout")
        ContentHiding.hideInstantly(".two-row-layout")
      }
      else if (dom.document.URL.contains(s"$TARGET_SCRIPT_VARIATION/")) { // What an ugly way to work with this for the time being
        ContentHiding.reveal(".two-row-layout")
      } else {
        ContentHiding.reveal(".one-row-layout")
      }

        val firstCharacterLine = targetCharacterLines.get(0)

        targetCharacterLines
          .click(ContentHiding.toggleContent _)

        val currentTargets = new CurrentTargets(firstCharacterLine.id, firstCharacterLine.id)

        jquery(".scroll-to-next-line")
          .click { _: JQueryEventObject => scrollToElementWithBuffer(_.nextLineId, Next, currentTargets) }

        jquery(".scroll-to-next-line-big")
          .click { _: JQueryEventObject => iterateToElement(_.nextLineId, 10, Next, currentTargets) }

        jquery(".scroll-to-previous-line")
          .click { _: JQueryEventObject => scrollToElementWithBuffer(_.previousLineId, Prev, currentTargets) }

        jquery(".scroll-to-previous-line-big")
          .click { _: JQueryEventObject => iterateToElement(_.previousLineId, 10, Prev, currentTargets) }

        targetCharacterLines.each((index, line) => ContentHiding.toggleContentInJqueryElement(line))
        targetCharacterLines.each((index, line) => jquery(line).addClass("targetCharacter"))


      })
  }

}




