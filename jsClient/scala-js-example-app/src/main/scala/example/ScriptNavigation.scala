package example

import org.scalajs.dom
import dom.document.getElementById
import org.scalajs.jquery.{JQuery, JQueryEventObject}

object ScriptNavigation {
  var targetCharacterLines: JQuery = _
  var previousLineId: String = ""
  var nextLineId: String = ""

  val TARGET_SCRIPT_VARIATION =
//    "full_script_with_lines_highlighted"
  "completely_blank_lines_with_spoken_cues"

  private def iterateToElement(targetId: () => String, numSteps: Int, scrollingTarget: ScrollingTarget): Unit = {
    val targetLine = getElementById(targetId())
    nextLineId = targetLine.getAttribute("data-next-line")
    previousLineId = targetLine.getAttribute("data-previous-line")

    if( numSteps > 0)
      iterateToElement(targetId, numSteps -1, scrollingTarget)
    else
      scrollToElementWithBuffer(targetId, scrollingTarget)
  }

  private def scrollToElementWithBuffer(targetId: () => String, scrollingTarget: ScrollingTarget): Unit = {
    val fixedTargetId: String = targetId()

    val targetLine = getElementById(fixedTargetId)
    val targetLineTyped = ConnectedLine(targetLine)

    nextLineId = targetLineTyped.nextLineId
    previousLineId = targetLineTyped.previousLineId

    if(dom.document.URL.contains(s"$TARGET_SCRIPT_VARIATION/")) { // What an ugly way to work with this for the time being
      targetLineTyped.cueLine.scrollIntoView(true)

      SpeechClient.speak(targetLineTyped.cueLineContent)
    } else {
      targetLineTyped.dataScrollBuffer.scrollIntoView(false)
    }
  }

  def setupScriptNavigationOrHideControls() {
    // TODO get query param character here.
    // dom.document.URL
    // val fields=temp_url.split("&").map(js.URIUtils.decodeURIComponent)
    val targetCharacterWithPrefix = dom.window.location.toString.dropWhile(_ != '=')
    val targetCharacterAttempt: Option[String] = if (!targetCharacterWithPrefix.isEmpty) Some(targetCharacterWithPrefix.tail) else None
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
        nextLineId = firstCharacterLine.id
        previousLineId = firstCharacterLine.id

        def getPrevLineId(): String = previousLineId

        def getNextLineId(): String = nextLineId

        targetCharacterLines
          .click(ContentHiding.toggleContent _)

        jquery(".scroll-to-next-line")
          .click { _: JQueryEventObject => scrollToElementWithBuffer(getNextLineId, Next) }

        jquery(".scroll-to-next-line-big")
          .click { _: JQueryEventObject => iterateToElement(getNextLineId, 10, Next) }

        jquery(".scroll-to-previous-line")
          .click { _: JQueryEventObject => scrollToElementWithBuffer(getPrevLineId, Prev) }

        jquery(".scroll-to-previous-line-big")
          .click { _: JQueryEventObject => iterateToElement(getPrevLineId, 10, Prev) }

        targetCharacterLines.each((index, line) => ContentHiding.toggleContentInJqueryElement(line))
        targetCharacterLines.each((index, line) => jquery(line).addClass("targetCharacter"))


      })
  }

}




