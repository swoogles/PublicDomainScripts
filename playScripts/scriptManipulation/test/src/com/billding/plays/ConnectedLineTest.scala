package com.billding.plays

import utest._

object ConnectedLineTest extends TestSuite {
  val JULIET = PlayCharacter("JULIET")
  val ROMEO = PlayCharacter("ROMEO")

  val manyLines =
    List(
      StartLine(0),
      BlankLine(1),
      ManipulatedLine(ROMEO, "Manipulated line 2", 2, "M l 2"),
      BlankLine(3),
      ManipulatedLine(JULIET, "Juliet line", 4, "S l 4"),
      BlankLine(5),
      ManipulatedLine(ROMEO, "Manipulated line 6", 6, "M l 6"),
      BlankLine(7),
      ManipulatedLine(JULIET, "Juliet line 8.", 8, "S l 8"),
      BlankLine(9),
      ManipulatedLine(ROMEO, "Manipulated line 10", 10, "M l 10"),
      BlankLine(11),
      ManipulatedLine(JULIET, "Juliet line 12.", 12, "S l 12"),
      BlankLine(13),
      ManipulatedLine(ROMEO, "Manipulated line 14", 14, "M l 14"),
      BlankLine(15),
      EndLine(16)
    )


  override def tests: Tests = Tests {
    /*
    'FindSpokenLineBefore - {
      assert(
        ConnectedLine.findCueLineBefore(6, manyLines)
          == Option(ManipulatedLine(JULIET, "Juliet line", 4, "S l 4"))
      )

    }
    */

    val OTHELLO = PlayCharacter("OTHELLO")
    val IAGO = PlayCharacter("IAGO")

    val othelloCharacters = List(
    OTHELLO,
    IAGO
    )
    val unconnectedShakespeareLines = List(
          ManipulatedLine(OTHELLO, "Othello line 0", 0, "O l 0"),
          ManipulatedLine(IAGO, "Iago line 1", 1, "I l 1"),
          ManipulatedLine(OTHELLO, "Othello line 2", 2, "O l 2"),
          ManipulatedLine(IAGO, "Iago line 3", 3, "I l 3"),
          ManipulatedLine(OTHELLO, "Othello line 4", 4, "O l 4"),
          ManipulatedLine(IAGO, "Iago line 5", 5, "I l 5"),
          StageDirection("Play ends", 6), // Needed so last Iago line can be targeted with a buffer
          StageDirection("Play *really* ends", 7), // Needed so last Iago line can be targeted with a buffer
    )

    'ConnecteShakespeareLines - {
      val connectedResults = ConnectedLine.makeConnectedLinesShakespeare(IAGO).apply(unconnectedShakespeareLines)
      assert( connectedResults ==
      List(
      ConnectedLine(

        ManipulatedLine(PlayCharacter("OTHELLO", "a character"), "Othello line 0", 0, "O l 0"),    
  ManipulatedLine(PlayCharacter("IAGO", "a character"), "Iago line 1", 1, "I l 1"),
  Some(ManipulatedLine(PlayCharacter("OTHELLO", "a character"), "Othello line 0", 0, "O l 0")),
  ManipulatedLine(PlayCharacter("IAGO", "a character"), "Iago line 1", 1, "I l 1"),
  ManipulatedLine(PlayCharacter("OTHELLO", "a character"), "Othello line 2", 2, "O l 2"),
  ManipulatedLine(PlayCharacter("IAGO", "a character"), "Iago line 3", 3, "I l 3"),
  ManipulatedLine(PlayCharacter("OTHELLO", "a character"), "Othello line 4", 4, "O l 4")
),
ConnectedLine(                                                                                 
  ManipulatedLine(PlayCharacter("OTHELLO", "a character"), "Othello line 0", 0, "O l 0"),
  ManipulatedLine(PlayCharacter("IAGO", "a character"), "Iago line 1", 1, "I l 1"),
  Some(ManipulatedLine(PlayCharacter("OTHELLO", "a character"), "Othello line 2", 2, "O l 2")),
  ManipulatedLine(PlayCharacter("IAGO", "a character"), "Iago line 3", 3, "I l 3"),
  ManipulatedLine(PlayCharacter("OTHELLO", "a character"), "Othello line 4", 4, "O l 4"),
  ManipulatedLine(PlayCharacter("IAGO", "a character"), "Iago line 5", 5, "I l 5"),
  StageDirection("Play ends", 6)
),
ConnectedLine(
  ManipulatedLine(PlayCharacter("OTHELLO", "a character"), "Othello line 2", 2, "O l 2"),
  ManipulatedLine(PlayCharacter("IAGO", "a character"), "Iago line 3", 3, "I l 3"),
  Some(ManipulatedLine(PlayCharacter("OTHELLO", "a character"), "Othello line 4", 4, "O l 4")),
  ManipulatedLine(PlayCharacter("IAGO", "a character"), "Iago line 5", 5, "I l 5"),
  StageDirection("Play ends", 6),
  ManipulatedLine(PlayCharacter("IAGO", "a character"), "Iago line 5", 5, "I l 5"),
  StageDirection("Play ends", 6)

      )
      )
      )
    }

    'ConnectAllLines - {
      val connectedResults: List[Either[Line, ConnectedLine]] = ConnectedLine.makeAllConnectedLines(unconnectedShakespeareLines)
    }
    'GroupLines -{
      val unconnectedLinesByIndex = 
      unconnectedShakespeareLines
      .map(unconnectedLine => (unconnectedLine.index, unconnectedLine))
      .toMap

    }

  }
}
