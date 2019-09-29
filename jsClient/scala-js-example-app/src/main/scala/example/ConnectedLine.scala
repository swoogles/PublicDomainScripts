package example

import org.scalajs.dom.raw.Element
import org.scalajs.dom.document.getElementById

case class ConnectedLine(
    dataScrollBuffer: Element,
    previousLineId: String,
    cueLine: Element,
    cueLineContent: String,
    nextLineId: String
)

object ConnectedLine {
  def apply(
      element: Element
  ): ConnectedLine =
    // TODO nullableAttributeToOptionalString
    // TODO null checks
    new ConnectedLine(
      getElementById(element.getAttribute("data-scroll-buffer")),
      element.getAttribute("data-previous-line"),
      getElementById(element.getAttribute("data-cue-line-id")), // cue line id,
      element.getAttribute("data-cue-line"),
      element.getAttribute("data-next-line")
    )

}
