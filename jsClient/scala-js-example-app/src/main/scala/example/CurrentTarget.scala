package example

import org.scalajs.dom.document.getElementById

class CurrentTarget(var connectedLine: ConnectedLine) {
  def updateTarget(targetId: CurrentTarget => String): Unit =
    this.connectedLine = ConnectedLine(getElementById(targetId(this)))
}
