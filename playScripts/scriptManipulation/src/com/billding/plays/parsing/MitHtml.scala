package com.billding.plays.parsing

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

import com.billding.plays.SpokenLine
import com.billding.plays.StageDirection
import com.billding.plays.PlayCharacter
import com.billding.plays.ActBreak
import com.billding.plays.SceneBreak
import com.billding.plays.Line

/*
  TODO
    -Make sure that any block parses to one, and only one, output type.
 */
object MitHtml {

  val browser = JsoupBrowser()

  def typedLinesFromRawScript(rawScriptText: String): List[Line] = {

    rawScriptText
      .split("\\n\\n")
      .flatMap(MitHtml.normalize)
      .filter(!_.trim.isEmpty)
      .toList
      .zipWithIndex
      .map { case (line, index) => MitHtml.Conversion.convert(line, index) }

  }

  object Conversion {
    // Assumes it's normalized
    def convert(input: String, lineNumber: Int): Line = {
      if (Recognition.isActTransition(input)) {
        parseActBreak(input, lineNumber)
      } else if (Recognition.isSceneTransition(input)) {
        parseSceneBreak(input, lineNumber)
      } else if (Recognition.isSpokenLine(input)) {
        parseSpokenLineTyped(input, lineNumber)
      } else if (Recognition.isStageDirection(input)) {
        parseStageDirection(input, lineNumber)
      } else if (Recognition.isChorusSpokenLine(input)) {
        parseChorusLine(input, lineNumber)
      } else {
        // Should throw an error here
        throw new RuntimeException("Unrecognized line: " + input)
      }

    }
  }

  def normalize(input: String): Seq[String] = {
    val lines = input.split("\n")
    if (lines.length == 1) {
      Seq(input)
    } else if (Recognition.isHtmlBeginning(input)) {
      Seq() // Don't need it!
    } else if (Recognition.isOpeningTable(input)) {
      Seq() // Don't want the table, just drop it.
    } else if (Recognition.isActTransition(input)) {
      splitSceneTransitionWithActBreak(input)
    } else if (Recognition.isSceneTransition(input)) {
      splitSceneTransitionWithoutActBreak(input)
      // TODO The conditions below here are order-dependent!
      // If I put these conditions first, they would match before the more significant Act/Scene checks happened.
    } else if (Recognition.isSpokenLine(input)) {
      Seq(input)
    } else if (Recognition.isStageDirection(input)) {
      Seq(input)
    } else if (Recognition.isChorusSpokenLine(input)) {
      Seq(input)
    } else if (input.trim.isEmpty) {
      Seq()
    } else {
      throw new RuntimeException("Unrecognized html: " + input)
    }

  }

  object Recognition {
    def isHtmlBeginning(input: String): Boolean = {
      input.contains("!DOCTYPE")
    }

    def isOpeningTable(input: String): Boolean =
      browser
        .parseString(input)
        .tryExtract(element("table"))
        .isDefined

    def isSceneTransition(input: String): Boolean =
      browser
        .parseString(input)
        .tryExtract(element("h3")) match {
        case None => false
        case Some(value) =>
          value.text.contains("SCENE") || value.text.contains("PROLOGUE")
      }

    def isActTransition(input: String): Boolean =
      browser
        .parseString(input)
        .tryExtract(element("h3")) match {
        case None        => false
        case Some(value) => value.text.contains("ACT")
      }

    def isSpokenLine(input: String): Boolean = {
      browser
        .parseString(input)
        .tryExtract(element("a[name^=speech]"))
        .isDefined
    }

    def isStageDirection(input: String): Boolean =
      browser
        .parseString(input)
        .tryExtract(element("i"))
        .isDefined

    def isChorusSpokenLine(input: String): Boolean =
      browser
        .parseString(input)
        .tryExtract(element("blockquote"))
        .isDefined

  }

  def parseSpokenLineTyped(input: String, lineNumber: Int): SpokenLine = {
    val parsedHtml = browser.parseString(input)
    val firstElement = parsedHtml >> element("a[name^=speech]")
    val textBlockAttempt = parsedHtml >?> element("blockquote")
    val textBlock = textBlockAttempt
      .map(textBlock => (textBlock >> elements("a")).map(_.text).mkString(" "))
      .getOrElse("") // Handle weird cases where it's a character name and no text/action attributed to them

    SpokenLine(
      PlayCharacter(firstElement.text),
      textBlock,
      lineNumber
    )
  }

  def parseChorusLine(input: String, lineNumber: Int): SpokenLine = {
    val parsedHtml = browser.parseString(input)
    val textBlock = parsedHtml >> element("blockquote")

    SpokenLine(
      PlayCharacter("CHORUS"),
      (textBlock >> elements("a")).map(_.text).mkString(" "),
      lineNumber
    )
  }

  def parseStageDirection(input: String, lineNumber: Int): StageDirection = {
    val parsedHtml = browser.parseString(input)
    val italicSection = parsedHtml >> element("i")
    StageDirection(italicSection.text, lineNumber)
  }

  def parseActBreak(input: String, lineNumber: Int): ActBreak = {
    val parsedHtml = browser.parseString(input)
    val actHeader = parsedHtml >> element("h3")
    ActBreak(actHeader.text, lineNumber)
  }

  def parseSceneBreak(input: String, lineNumber: Int): SceneBreak = {
    val parsedHtml = browser.parseString(input)
    val actHeader = parsedHtml >> element("h3")
    SceneBreak(actHeader.text, lineNumber)
  }

  def splitSceneTransitionWithoutActBreak(input: String): Seq[String] = {
    val lines = input.split("\n")
    if (lines.length == 1) {
      Seq(input)
    } else {
      val splitIndex = lines.indexWhere(Recognition.isSceneTransition)
      val (linesBeforeSceneBreak, sceneBreakAndBeyond) =
        lines.splitAt(splitIndex)
      normalize(linesBeforeSceneBreak.mkString("\n")) ++
        Seq(
          sceneBreakAndBeyond.head
        ) ++ normalize(sceneBreakAndBeyond.tail.mkString("\n"))
    }
  }

  def splitSceneTransitionWithActBreak(input: String): Seq[String] = {
    val lines = input.split("\n")
    if (lines.length == 1) {
      Seq(input)
    } else {
      val splitIndex =
        lines.indexWhere(Recognition.isActTransition)

      val (linesBeforeSceneBreak, actBreakAndBeyond) =
        lines.splitAt(splitIndex)

      val (actBreak :: openingStageDirection) = actBreakAndBeyond.toList

      normalize(linesBeforeSceneBreak.mkString("\n")) ++
        Seq(actBreak) ++
        normalize(openingStageDirection.mkString("\n"))
    }
  }

}
