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

object GutenburgHtml {
  val browser = JsoupBrowser()

  def parseRawHtml(input: String): Unit = {
    (browser
      .parseString(input) >> element("body")).children
      .filter(element => element.tagName != "pre")
      .dropWhile(child => child.tagName != "h2" || !child.text.contains("ACT"))
      .take(20)
      .foreach(child => child)
//      .foreach(child => println("Child: " + child))
  }

  def parseSpokenLine(input: String, lineNumber: Int): SpokenLine = {
    val parsedHtml = browser.parseString(input) >> element("p")
    val characterName =
      (parsedHtml >> element("b")).text.reverse.replaceFirst(".", "").reverse
    val spokenText = parsedHtml.text.replaceFirst(characterName + ".", "").trim

    SpokenLine(
      PlayCharacter(characterName),
      spokenText,
      lineNumber
    )
  }

  def parseStageDirection(input: String, lineNumber: Int): StageDirection = {
    val parsedHtml = (browser.parseString(input) >> element("p")).text
      .replace("[", "")
      .replace("]", "")
    StageDirection(
      parsedHtml,
      lineNumber
    )
  }

}
