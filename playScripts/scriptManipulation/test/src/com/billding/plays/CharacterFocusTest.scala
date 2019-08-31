package com.billding.plays

import utest._

object CharacterFocusTest extends TestSuite {
  val tests = Tests {

    val JULIET = PlayCharacter("JULIET")
    val CLOWN = PlayCharacter("CLOWN")
    val ROMEO = PlayCharacter("ROMEO")

    'GetSingleLineOfContext - {
      val lines: List[SpokenWords] =
        List(
          SpokenLine(CLOWN, "Clown line", 0),
          SpokenLine(JULIET, "Juliet line.", 1),
          ManipulatedLine(ROMEO, "Manipulated line", 2, "V l 2"),
        )
      val numWordsForContext = 2
      val result = CharacterFocus.getContextForLine(2, lines, numWordsForContext)
      assert(
        result.length == 2
      )
    }
  }
}
