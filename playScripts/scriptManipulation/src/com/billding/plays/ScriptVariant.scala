package com.billding.plays

case class ScriptVariant(
    name: String,
    transformation: String => String
)
object ScriptVariant {

  val FULL_TEXT =
    ScriptVariant(
      "full_script_with_lines_highlighted",
      x => x
    )

  val FIRST_LETTER_OF_EACH_WORD =
    ScriptVariant(
      "first_letter_of_each_word",
      ContentManipulation.firstLetterOfEachWord
    )

  val FIRST_WORD_OF_EACH_SENTENCE =
    ScriptVariant(
      "first_word_of_each_sentence",
      ContentManipulation.firstWordOfEachSentence
    )

  val BLANK_LINES_WITH_SPOKEN_CUES =
    ScriptVariant(
      "completely_blank_lines_with_spoken_cues",
      ContentManipulation.completelyBlank
    )

}
