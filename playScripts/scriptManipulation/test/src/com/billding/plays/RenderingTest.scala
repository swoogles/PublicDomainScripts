package com.billding.plays

import utest._

object RenderingTest extends TestSuite{
  override def tests = Tests {
    val targetCharacter = PlayCharacter("speaker")
    val otherCharacter = PlayCharacter("otherCharacter")
    'TargetCharacterLineToHtml - {
      val input = ManipulatedLine(targetCharacter, "this is being spoken by the target character", 1, "t i b s b t t c")
      val result = Rendering.toHtml(input)
      val expected =
          s"""<div class="manipulatedLine padded ${targetCharacter.name}" id=\"script-element-1\">""" +
            "<span><b>speaker. </b></span>" +
            "<span style=\"display: none;\" class=\"full-content\">" +
            "this is being spoken by the target character</span>" +
            "<span style=\"display: inline;\" class=\"reduced-content\">t i b s b t t c</span></div>";
      assert(result.toString() == expected)
    }
    'OtherCharacterLineToHtml - {
      val input = SpokenLine(otherCharacter, "this is being spoken by a different character", 2)
      val result = Rendering.toHtml(input)
      val expected =
        "<div class=\"padded\" id=\"script-element-2\">" +
          "<span><b>otherCharacter. </b></span>" +
          "<span>this is being spoken by a different character</span>" +
        "</div>"

      assert(result.toString() == expected)
    }

    val JULIET = PlayCharacter("JULIET")
    val CLOWN = PlayCharacter("CLOWN")
    val ROMEO = PlayCharacter("ROMEO")

    val manyLines =
      List(
        StartLine(0),
        BlankLine(1),
        ManipulatedLine(ROMEO, "Manipulated line 2", 2, "M l 2"),
        BlankLine(3),
        SpokenLine(CLOWN, "Clown line", 4),
        BlankLine(5),
        ManipulatedLine(ROMEO, "Manipulated line 6", 6, "M l 6"),
        BlankLine(7),
        SpokenLine(JULIET, "Juliet line 8.", 5),
        BlankLine(9),
        ManipulatedLine(ROMEO, "Manipulated line 10", 10, "M l 10"),
        BlankLine(11),
        SpokenLine(JULIET, "Juliet line 12.", 12),
        BlankLine(13),
        ManipulatedLine(ROMEO, "Manipulated line 14", 14, "M l 14"),
        BlankLine(15),
        EndLine(16)
      )

    'CreateUrlForFile - {
       println("urlForFile: " + Rendering.getrelativeUrl("/Repositories/hugo/play9592/content/generated/ROMEO/first_letter_of_each_word.html"))
    }

  }

}
