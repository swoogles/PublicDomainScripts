package example

import example.WebSpeechFacade.SpeechSynthesisUtterance

object SpeechClient {

  def speak(verbiage: String) = {
    WebSpeechFacade.SpeechSynthesis.cancel() // Interrupt previous cue, don't queue them up.
    val utterance = new SpeechSynthesisUtterance(verbiage)
    WebSpeechFacade.SpeechSynthesis.speak(utterance)
  }

}
