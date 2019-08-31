package com.billding.plays

/**
  * These are the different "end-product" Conversions I can apply to the raw script contet
  */
trait ScriptConversions {
  def firstLetterOfEachWord(line: String): String
  def completelyBlank(line: String): String
  def firstWordOfEachSentence(line: String): String
}
