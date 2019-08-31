package com.billding.plays

case class ConnectedLine(
    previousBuffer: Line,
    previousLine: ManipulatedLine,
    cueLine: Option[ManipulatedLine],
    self: ManipulatedLine,
    selfScrollForwardBuffer: Line,
    nextLine: ManipulatedLine,
    nextBuffer: Line
)

object ConnectedLine {

  def makeAllConnectedLines(
      lines: List[Line]
  ): List[Either[Line, ConnectedLine]] = { // TODO should this return a List[Line] => List[ConnectedLine] ?
    val allCharactersDynamic = FullPlayProcesses.getCharactersWithSpokenLines(
      lines
    ) // TODO Cyclic dependency!!!!!!!!
    val connectedLinesByCharacter =
      for (character <- allCharactersDynamic) yield {
        makeConnectedLinesShakespeare(character).apply(lines)
      }
    combineAllConnectedLines(lines, connectedLinesByCharacter)
  }

  private def combineAllConnectedLines(
      originalLines: List[Line], // Original text, should only retain StageDirections & other meta lines
      connectedLines: List[List[ConnectedLine]]
  ): List[Either[Line, ConnectedLine]] = {
    val connectedLinesByIndex = makeIndexMapOfConnectedLines(connectedLines)
    originalLines.map { line =>
      connectedLinesByIndex
        .get(line.index)
        .map(connectedLine => Right(connectedLine))
        .getOrElse(Left(line))
    }
  }

  private def makeIndexMapOfConnectedLines(
      connectedLines: List[List[ConnectedLine]]
  ): Map[Int, ConnectedLine] = {
    val mapsByIndex =
      connectedLines.map(
        connectedLinesForCharacter =>
          connectedLinesForCharacter
            .map(
              unconnectedLine => (unconnectedLine.self.index, unconnectedLine)
            )
            .toMap
      )
    mapsByIndex.reduce(_ ++ _)

  }

  def makeConnectedLinesShakespeare(
      character: PlayCharacter
  ): List[Line] => List[ConnectedLine] = { lines =>
    connectManipulatedLinesWithHtmlOriginalSource(
      lines,
      ContentFiltering.getCharactersLinesSimpler(lines, character)
    )
  }

  def findCueLineBeforeCharacterBased(
      targetIndex: Int,
      lines: List[Line],
      targetCharacter: PlayCharacter
  ): Option[ManipulatedLine] = {
    val backwardsLinesToSearch = lines.take(targetIndex).reverse
    val spokenLines: List[ManipulatedLine] =
      backwardsLinesToSearch
        .collect { case manipulatedLine: ManipulatedLine => manipulatedLine }
        .filter(_.character != targetCharacter)

    spokenLines.headOption
  }

  // This is the ugliest POS in the entire program.
  private def connectManipulatedLinesWithHtmlOriginalSource(
      lines: List[Line],
      targetLines: List[ManipulatedLine]
  ): List[ConnectedLine] = {
    val offset = 1
    val firstLineIndex = targetLines(0).index
    val targetCharacter = targetLines.head.character // TODO Eh, don't like this
    val firstCueLine =
      findCueLineBeforeCharacterBased(firstLineIndex, lines, targetCharacter)

    if (targetLines.length == 1) {
      List(
        ConnectedLine(
          lines(firstLineIndex - offset),
          targetLines(0),
          firstCueLine,
          targetLines(0),
          lines(firstLineIndex + offset),
          targetLines(0),
          lines(targetLines(0).index + offset)
        )
      )
    } else if (targetLines.length == 2) {
      List(
        ConnectedLine(
          lines(firstLineIndex - offset),
          targetLines(0),
          firstCueLine,
          targetLines(0),
          lines(firstLineIndex + offset),
          targetLines(0),
          lines(targetLines(0).index + offset)
        ),
        ConnectedLine(
          lines(targetLines(targetLines.length - 2).index - offset),
          targetLines(targetLines.length - 2),
          findCueLineBeforeCharacterBased(
            targetLines.last.index,
            lines,
            targetCharacter
          ),
          targetLines.last,
          lines(targetLines.last.index + offset),
          targetLines.last,
          lines(targetLines.last.index + offset)
        )
      )
    } else {
      val previousLineBuffer =
        if (firstLineIndex == 0)
          firstLineIndex
        else
          firstLineIndex - offset
      val firstLine =
        ConnectedLine(
          lines(previousLineBuffer),
          targetLines(0),
          firstCueLine,
          targetLines(0),
          lines(firstLineIndex + offset),
          targetLines(1),
          lines(targetLines(1).index + offset)
        )

      val lastLineIndex: Int = lines.last.index

      val middleLines: List[ConnectedLine] =
        targetLines
          .sliding(3)
          .map { lineGroup: List[ManipulatedLine] =>
            {
              val previousLineBuffer =
                if (lineGroup(0).index == 0)
                  lineGroup(0).index
                else
                  lineGroup(0).index - offset
              ConnectedLine(
                lines(previousLineBuffer),
                lineGroup(0),
                findCueLineBeforeCharacterBased(
                  lineGroup(1).index,
                  lines,
                  targetCharacter
                ),
                lineGroup(1),
                lines(lineGroup(1).index + offset),
                lineGroup(2),
                lines(
                  if ((lineGroup(2).index + offset) > lastLineIndex)
                    lastLineIndex
                  else
                    lineGroup(2).index + offset
                )
              )
            }
          }
          .toList

      val nextLineForLastLine =
        if (targetLines.last.index + offset > lastLineIndex)
          lastLineIndex
        else
          targetLines.last.index + offset

      val lastLine =
        ConnectedLine(
          lines(targetLines(targetLines.length - 2).index - offset),
          targetLines(targetLines.length - 2),
          findCueLineBeforeCharacterBased(
            targetLines.last.index,
            lines,
            targetCharacter
          ),
          targetLines.last,
          lines(nextLineForLastLine),
          targetLines.last,
          lines(nextLineForLastLine)
        )
      List(firstLine) ::: middleLines ::: List(lastLine)
    }
  }

}
