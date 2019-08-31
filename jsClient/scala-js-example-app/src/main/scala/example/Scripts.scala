package example

import org.scalajs.dom
import dom.document.getElementById
import org.scalajs.jquery.{JQuery, JQueryEventObject, JQueryStatic}
import org.scalajs.dom.Element

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSImport}

object ScriptNavigation {
  var targetCharacterLines: JQuery = _
  var previousLineId: String = ""
  var nextLineId: String = ""

  private def hide(element: JQuery): Unit =
    element.css("display", "none")

  private def displayInline(element: JQuery): Unit =
    element.css("display", "inline")

  private def toggleContentInJqueryElement(element: Element): Unit = {
      val fullContent: JQuery = jquery(element).children(".full-content")
      val reducedContent = jquery(element).children(".reduced-content")
      if (fullContent.css("display") == "none") {
        displayInline(fullContent)
        hide(reducedContent)
      } else {
        hide(fullContent)
        displayInline(reducedContent)
      }
  }

  private def toggleContent( eventObject:  JQueryEventObject): js.Any = {
      val self = jquery("#" + eventObject.delegateTarget.id)
      val fullContent: JQuery = self.children(".full-content")
      val reducedContent = self.children(".reduced-content")
      if (fullContent.css("display") == "none") {
        displayInline(fullContent)
        hide(reducedContent)
      } else {
        hide(fullContent)
        displayInline(reducedContent)
      }
    }

  sealed trait ScrollingTarget {
    val token: String
  }
  case object Prev extends ScrollingTarget {
    val token = "previous"

  }
  case object Next extends ScrollingTarget {
    val token = "next"
  }

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
    println("fresh setup with watch!!!")
    val targetCharacterWithPrefix = dom.window.location.toString.dropWhile(_ != '=')
    val targetCharacter = if (!targetCharacterWithPrefix.isEmpty) targetCharacterWithPrefix.tail else "NO_TARGET_CHARACTER"
    println("crudely retrieved character: " + targetCharacter)

    val targetCharacterLines = jquery(s".$targetCharacter")
    println("Number of character lines: " + targetCharacterLines.length)

    if (targetCharacterLines.length == 0) { // There are no characters, so we're not viewing a script that needs controls.
      jquery(".one-row-layout").hide(0)
      jquery(".two-row-layout").hide(0)
    }
    else if(dom.document.URL.contains(s"$TARGET_SCRIPT_VARIATION/")) { // What an ugly way to work with this for the time being
      println("Should show two row layout")
      jquery(".two-row-layout").attr("hidden",false)
    } else {
      println("Should show one row layout")
      jquery(".one-row-layout").attr("hidden",false)
    }
    println("HI!")


    if (targetCharacterLines.length == 0) {
      // It's not a script, so there's no need to setup the controls.
    } else {
      val firstCharacterLine = targetCharacterLines.get(0)
      nextLineId = firstCharacterLine.id
      previousLineId = firstCharacterLine.id

      def getPrevLineId(): String = previousLineId

      def getNextLineId(): String = nextLineId

      targetCharacterLines
        .click(toggleContent _)

      jquery(".scroll-to-next-line")
        .click { _: JQueryEventObject => scrollToElementWithBuffer(getNextLineId, Next) }

      jquery(".scroll-to-next-line-big")
        .click { _: JQueryEventObject => iterateToElement(getNextLineId, 10, Next) }

      jquery(".scroll-to-previous-line")
        .click { _: JQueryEventObject => scrollToElementWithBuffer(getPrevLineId, Prev) }

      jquery(".scroll-to-previous-line-big")
        .click { _: JQueryEventObject => iterateToElement(getPrevLineId, 10, Prev) }

    targetCharacterLines.each( (index, line) => toggleContentInJqueryElement(line))
    targetCharacterLines.each( (index, line) => jquery(line).addClass("targetCharacter"))

    }
  }

}




