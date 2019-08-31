package example

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

/**
  * This can tell you if the user is on a mobile browser
  */
@js.native
@JSGlobal("navigator")
object Navigator extends js.Object {
  val userAgent: String = js.native
}
