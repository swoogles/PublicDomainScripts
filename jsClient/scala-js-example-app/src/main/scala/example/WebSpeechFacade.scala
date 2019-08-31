package example

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSName}

object WebSpeechFacade {

  @js.native
  trait SpeechSynthesisVoice extends js.Object {
    val voiceURI: String = js.native
  }

  @js.native
  @JSGlobal("SpeechSynthesisUtterance")
  class SpeechSynthesisUtterance(var text: String) extends js.Object {
    var voice: SpeechSynthesisVoice = js.native
  }

  @js.native
  @JSGlobal("speechSynthesis")
  object SpeechSynthesis extends js.Object {
    def cancel(): Unit = js.native
    def speak(msg: SpeechSynthesisUtterance): Unit = js.native
    def getVoices(): js.Array[SpeechSynthesisVoice] = js.native
  }
}

