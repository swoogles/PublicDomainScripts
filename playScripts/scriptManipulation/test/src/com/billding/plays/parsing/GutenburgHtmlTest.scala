package com.billding.plays.parsing
import utest._
import com.billding.plays.UnsafeWorld
import com.billding.plays.SpokenLine
import com.billding.plays.StageDirection
import com.billding.plays.PlayCharacter
import better.files.Dsl.cwd


object ProjectGutenburgTest extends TestSuite {
  val workingDirectory = cwd
  val unsafeWorld = new UnsafeWorld(workingDirectory)

  override def tests: Tests = Tests {
      'ParseHtml - {
          val nameOfFileToParse = "the_importance_of_being_earnest.htm"
    val fileContent: String =
      unsafeWorld.getFileAsOneBigString(nameOfFileToParse, "Gutenburg")
          GutenburgHtml.parseRawHtml(fileContent)

      }

      'ParseSpokenLine - {
val rawLine = """<p><b>Lady Bracknell.</b>&nbsp; Where did the charitable
gentleman who had a first-class ticket for this seaside resort
find you?</p>"""
val lineNumber = 5

val expectedResult = 
    SpokenLine(
      PlayCharacter("Lady Bracknell"),
      "Where did the charitable gentleman who had a first-class ticket for this seaside resort find you?",
      lineNumber
    )
val actual__Result = GutenburgHtml.parseSpokenLine(rawLine, lineNumber)
assert( actual__Result == expectedResult)
      }

      'ParseStageDirection - {
          val input = """<p>[Enter <b>Lane</b>.]</p>"""
val lineNumber = 5
val expectedResult = 
    StageDirection(
      "Enter Lane.",
      lineNumber
    )
val actual__Result = GutenburgHtml.parseStageDirection(input, lineNumber)
assert( actual__Result == expectedResult)
      }
  }
}