package com.billding.plays
import scala.collection.immutable.Seq

object ContentManipulation extends ScriptConversions {
  private val punctuation: Seq[Char] = List('.', '!', '-', '?', ',')

  private def endsWithPunctuation(word: String): Boolean =
    punctuation.exists { punctuationMark =>
      word.endsWith(punctuationMark.toString)
    }

  def firstLetterOfWord(word: String): String = {
    val leadingQuote: String = if (word.head == '"') "\"" else ""
    val startIndex = if (leadingQuote.length > 0) 1 else 0

    val trailingQuote: String = if (word.last == '"') "\"" else ""
    val endIndex =
      if (trailingQuote.length > 0) word.length - 1 else word.length
    val quoteStrippedWord = word.substring(startIndex, endIndex)
    val innerWordResult =
      if (quoteStrippedWord == "...") {
        quoteStrippedWord
      } else if (endsWithPunctuation(quoteStrippedWord)) // Preserve punctuation.
        quoteStrippedWord.head + quoteStrippedWord.last.toString
      else
        quoteStrippedWord.head

    leadingQuote + innerWordResult + trailingQuote
  }

  def wordsFrom(line: String): List[String] =
    line
      .split("\\s+")
      .toList
      .filter(_.nonEmpty)

  def linePrep(line: String): String = // This should indicate more clearly that it's going to *ditch* the first word.
    wordsFrom(line.trim) // Right now, it only makes sense to use when you have a single-word character name at the start of the line. Sorry Female Admirer.
    .tail
      .mkString(" ")

  def firstLetterOfEachWord(line: String): String =
    wordsFrom(line)
      .map(firstLetterOfWord)
      .mkString(" ")

  private def splitIntoSentences(line: String): List[String] =
    if (line.isEmpty) {
      Nil
    } else {
      val (s1, s2) = line.span { !punctuation.contains(_) }
      if (!s2.isEmpty) { // mv punctuation to end of previous sentence.
        s1 + s2.head :: splitIntoSentences(s2.tail)
      } else {
        List(s1)
      }
    }

  def completelyBlank(line: String): String =
    ""

  private def normalizedSpacing(line: String): String =
    wordsFrom(line).mkString(" ")

  def firstWordOfEachSentence(line: String): String = {
    normalizedSentences(line)
      .map { sentence =>
        val (firstWord :: restOfSentence) = wordsFrom(sentence)
        val blackedOutWords =
          restOfSentence
            .map { word =>
              if (endsWithPunctuation(word))
                word
                  .map { letter =>
                    "_"
                  }
                  .dropRight(1)
                  .mkString("")
              else
                word
                  .map { letter =>
                    "_"
                  }
                  .mkString("")
            }
            .mkString(" ")

        // TODO rm blacked out punctuation mark, as it increases the masked length by 1 additional underscore
        if (restOfSentence.nonEmpty)
          firstWord + " " + blackedOutWords + restOfSentence.last.last
        else
          firstWord + " " + blackedOutWords
      }
      .mkString(" ")
  }

  def contextForCue(line: String): String = {
    // TODO spiffy this up. Handle ellipses properly.
    val sentences = normalizedSentences(line)
    if (sentences.isEmpty)
      ""
    else if (line.length < 150)
      line
    else if (sentences.length == 1)
      sentences.head
    else if (sentences.last.length > 40)
      sentences.last
    else
      sentences(sentences.length - 2) + sentences.last
  }

  private def normalizedSentences(line: String): List[String] =
    splitIntoSentences(
      normalizedSpacing(line)
    )

  def lastSentences(line: String, numSentences: Int): String =
    normalizedSentences(line)
      .takeRight(numSentences)
      .mkString(" ")
      .trim

  def lastSentence(line: String): String =
    normalizedSentences(line).last.trim

}
