package com.billding.plays

import utest._

object ParsingTest extends TestSuite {
  val tests = Tests {

    'DynamicCharacterList - {
      val characters = List(
        PlayCharacter("CHARLIE"),
        PlayCharacter("TAMMY"),
        PlayCharacter("RANDI")
      )

      val line = "RANDI. hi there. I'm Randi."
      val result = Parsing.manipulateSingleLine(line, 0, PlayCharacter("RANDI"), characters, ContentManipulation.firstLetterOfEachWord)
    }

    'StripStageDirections - {
      val line = "She went over here (points offstage) and then ... (cries)"
      assert(
        Parsing.stripStageDirections(line)
          == "She went over here and then ..."
      )
    }

  }
}
