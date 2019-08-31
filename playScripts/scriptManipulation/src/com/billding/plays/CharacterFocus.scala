package com.billding.plays

object CharacterFocus {
  def getContextForLine(
      targetLine: Int,
      lines: List[SpokenWords],
      numWordsForContext: Int
  ): List[SpokenWords] = {
    if (targetLine == 0) {
      List(
        lines(0)
      )
    } else if (lines(targetLine - 1).numberOfWords >= numWordsForContext) {
      List(
        lines(targetLine - 1),
        lines(targetLine)
      )
    } else {
      ???
    }

  }

}
