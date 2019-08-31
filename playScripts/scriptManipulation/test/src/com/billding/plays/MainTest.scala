package com.billding.plays

import utest._

object MainTest extends TestSuite{
  val JULIET = PlayCharacter("JULIET")
  val ROMEO = PlayCharacter("ROMEO")
  val tests = Tests{

    'ShowMyLinesOnly - {
      val script = List(
          ManipulatedLine(ROMEO, "Visible line 1", 0, "V l 1"),
          SpokenLine(JULIET, "Invisible line 1", 1),
          ManipulatedLine(ROMEO, "Visible line 2", 2, "V l 2"),
          SpokenLine(JULIET, "Invisible line 2", 3)
      )
      val results = ContentFiltering.getCharactersLines(script)
      assert(
        results == List(
          ManipulatedLine(ROMEO, "Visible line 1", 0, "V l 1"),
          ManipulatedLine(ROMEO, "Visible line 2", 2, "V l 2")
        )
      )
    }

  }


}