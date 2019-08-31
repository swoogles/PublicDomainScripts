package com.billding.plays

object Parsing {

  def manipulateScript(
      originalLines: List[Line],
      action: String => String
  ): List[Line] = {
    originalLines.map {
      case SpokenLine(character, content, index) =>
        ManipulatedLine(character, content, index, action.apply(content))
      case otherStuff => otherStuff
    }
  }

  def parseFullScript(
      rawScript: String,
      characters: List[PlayCharacter]
  ): List[Line] = {
    rawScript
      .split("\n")
      .toList
      .zipWithIndex
      .map {
        case (line, index) =>
          parseSingleLine(line, index, characters)
      }
  }

  def parseLinesWithDynamicCharacters(
      originalLines: List[String],
      targetCharacter: PlayCharacter,
      characters: List[PlayCharacter]
  ): List[Line] = {
    originalLines.zipWithIndex
      .map {
        case (line, index) =>
          parseSingleLine(line, index, characters)
      }
      .map {
        case SpokenLine(character, content, index)
            if character == targetCharacter =>
          ManipulatedLine(character, content, index, content)
        case x => x
      }
  }

  // TODO privatize this or stick it in a different class.
  def stripStageDirections(line: String): String = {
    if (line.contains("(")) {
      val openingParen = line.indexOf("(")
      val closingParen = line.indexOf(")")
      val beforeParens = line.substring(0, openingParen)
      val afterParens = line.substring(closingParen + 1)
      (beforeParens + stripStageDirections(afterParens)).trim
    } else {
      line.trim
    }
  }

  // TODO Strip out stage directions in here.
  private def parseSingleLine(
      originalLine: String,
      lineNumber: Int,
      characters: List[PlayCharacter]
  ): Line = {
    originalLine match {
      case beginLine if beginLine equals "Start" => StartLine(lineNumber)
      case otherCharacterLine
          if startsWithAnyCharacter(otherCharacterLine, characters) =>
        val character: String = otherCharacterLine.takeWhile(_ != '.') // Meh. It'd be better if this was extracted earlier
        SpokenLine(
          PlayCharacter(character),
          linePrep(otherCharacterLine),
          lineNumber
        )
      case togetherLine if togetherLine startsWith "ALL" =>
        CombinedSpokenLine(characters.toSet, togetherLine, lineNumber)
      case stageDirection if stageDirection startsWith "(" =>
        StageDirection(stageDirection, lineNumber)
      case emptyLine if emptyLine.isEmpty          => BlankLine(lineNumber)
      case pageBreak if pageBreak startsWith "---" => PageBreak(lineNumber)
      case pageNumberLine if pageNumberLine startsWith "Page" =>
        PageNumber(pageNumberLine.replaceAll("[^0-9]", "").toInt, lineNumber)
      case endLine if endLine equals "End" => EndLine(lineNumber)
    }
  }

  def manipulateSingleLine(
      originalLine: String,
      lineIndex: Int,
      targetCharacter: PlayCharacter,
      characters: List[PlayCharacter],
      action: String => String
  ): Line = {
    parseSingleLine(originalLine, lineIndex, characters) match {
      case SpokenLine(character, content, _)
          if character == targetCharacter => {
        ManipulatedLine(character, content, lineIndex, action.apply(content))
      }
      case x => x
    }
  }

  private def startsWithAnyCharacter(
      originalLine: String,
      characters: List[PlayCharacter]
  ): Boolean = {
    characters.exists(character => originalLine.startsWith(character.name))
  }

  def linePrep(line: String): String = {
    wordsFrom(
      stripStageDirections(line.trim)
    ).tail
      .mkString(" ")
  }

  def wordsFrom(line: String): List[String] = {
    line.split("\\s+").toList
  }

  def linePrepWithoutTrimmingBeginning(line: String): String = {
    wordsFrom(line.trim)
      .mkString(" ")
  }
}
