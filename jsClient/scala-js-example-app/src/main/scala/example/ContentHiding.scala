package example

import org.scalajs.dom.Element
import org.scalajs.jquery.{JQuery, JQueryEventObject}

import scala.scalajs.js

/*
  Some of the void method here are flummoxing me in my desire to transition everything to ZIO.
  I think the thing that's making it difficult is that my side effect is adding a click behavior to elements,
  which is then triggered when a user interacts with the page. That's an external user interacting with an
  external system, so I don't think it's possible for that inner click behavior to be in a monad.
    This might be exactly what scalajs-react solves
 */
object ContentHiding {
  private def hide(element: JQuery): Unit =
    element.css("display", "none")

  def hideInstantly(selector: String): JQuery =
    jquery(selector).hide(0)

  def reveal(selector: String): JQuery =
    jquery(selector).attr("hidden", false)

  private def displayInline(element: JQuery): Unit =
    element.css("display", "inline")

  def showReducedContentOfJqueryElement(element: Element): Unit = {
    val fullContent: JQuery = jquery(element).children(".full-content")
    val reducedContent = jquery(element).children(".reduced-content")
    hide(fullContent)
    displayInline(reducedContent)
  }

  def toggleContent(eventObject: JQueryEventObject): js.Any = {
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

}
