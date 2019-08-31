package com.billding.plays

sealed trait Line {
  val index: Int
}

sealed trait SpokenWords {
  val numberOfWords: Int
}

case class SpokenLine(character: PlayCharacter, content: String, index: Int)
    extends Line
    with SpokenWords {
  val numberOfWords: Int = ContentManipulation.wordsFrom(content).length
}

case class ManipulatedLine(
    character: PlayCharacter,
    content: String,
    index: Int,
    reducedContent: String
) extends Line
    with SpokenWords {
  val reducedContentOutput: String = character.name + ". " + reducedContent
  val numberOfWords: Int = ContentManipulation.wordsFrom(content).length
}

case class StageDirection(content: String, index: Int) extends Line

case class BlankLine(index: Int) extends Line

case class CombinedSpokenLine(
    characters: Set[PlayCharacter],
    content: String,
    index: Int
) extends Line

case class PageBreak(index: Int) extends Line

case class PageNumber(number: Int, index: Int) extends Line

case class StartLine(index: Int) extends Line

case class EndLine(index: Int) extends Line

// TODO should this extend line? These should be combined into SpokenLine's pretty quickly, so maybe not.
case class OpeningLine(character: PlayCharacter, content: String)
case class ContinuedLine(content: String)
case class ActBreak(content: String, index: Int) extends Line
case class SceneBreak(content: String, index: Int) extends Line
