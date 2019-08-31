package com.billding.plays

object ContentFiltering {
  def getCharactersLines(originalLines: List[Line]): List[ManipulatedLine] =
    originalLines.collect {
      case manipulatedLine: ManipulatedLine => manipulatedLine
    }

  def getCharactersLinesSimpler(
      originalLines: List[Line],
      targetCharacter: PlayCharacter
  ): List[ManipulatedLine] =
    originalLines
      .collect { case manipulatedLine: ManipulatedLine => manipulatedLine }
      .filter(_.character == targetCharacter)

}
