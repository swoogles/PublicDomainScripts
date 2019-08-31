package com.billding.plays

import com.billding.plays.parsing.MitHtml
import better.files.Dsl.cwd

object FullPlayProcesses {
  val workingDirectory = cwd
  val unsafeWorld = new UnsafeWorld(workingDirectory)

  def mainContents() = {

    romeoAndJuliet()
    aMidSummerNightsDream()
    muchAdoAboutNothing()
    hamlet()
    macbeth()
    kingLear()
    juliusCaesar()
    theTamingOfTheShrew()
    theComedyOfErrors()
    othello()
    // TODO Get my main class figured out / synced with this test
    val allPlays = unsafeWorld.getFilesInGeneratedDir()

    val playMenu = Rendering.listPlays(allPlays)

    unsafeWorld.writePlaySelectionMenu(playMenu.toString())
  }

  val ALL_SCRIPT_VARIANTS: Set[ScriptVariant] =
    Set(
      ScriptVariant.FIRST_LETTER_OF_EACH_WORD,
      ScriptVariant.BLANK_LINES_WITH_SPOKEN_CUES,
      ScriptVariant.FULL_TEXT,
      ScriptVariant.FIRST_WORD_OF_EACH_SENTENCE
    )

  def createTypedLines(
      lines: List[String],
      lineTyper: (String, Int) => Line
  ): List[Line] = {
    lines.zipWithIndex
      .map { case (line, index) => lineTyper(line, index) }
      .map {
        case SpokenLine(PlayCharacter(name, description), content, index) =>
          SpokenLine(
            PlayCharacter(name, description),
            content,
            index
          )
        case other => other
      }
  }

  def getCharactersWithSpokenLines(
      typedLines: List[Line]
  ): List[PlayCharacter] = {
    typedLines
      .flatMap {
        case SpokenLine(character, _, _)         => Some(character)
        case ManipulatedLine(character, _, _, _) => Some(character)
        case _                                   => None
      }
      .map {
        case PlayCharacter(name, description) =>
          PlayCharacter(name, description)
      }
      // Currently ignoring all group/multi-character lines
      .filter { character =>
        !(character.name.contains(",") || character.name.contains("&"))
      }
      .distinct
      .sortWith(_.name < _.name)
  }

  def playVariations(
      nameOfFileToParse: String,
      outputPlayName: String,
      scriptVariants: Set[ScriptVariant],
      scriptTyper: String => List[Line]
  ): Unit = {
    val fileContent: String =
      unsafeWorld.getFileAsOneBigString(nameOfFileToParse)

    val typedLinesOutter =
      scriptTyper(fileContent)
        .map { // TODO encapsulate this normalization of character names elsewhere
          case SpokenLine(PlayCharacter(name, description), content, index) =>
            SpokenLine(
              PlayCharacter(name, description),
              content,
              index
            )
          case other => other
        }

    val allCharactersDynamic = getCharactersWithSpokenLines(typedLinesOutter)

    println(s"Creating script variations for $outputPlayName")
    for (scriptVariant <- scriptVariants) {
      println(s"   -${scriptVariant.name}")

      val manipulatedScript: List[Line] = Parsing.manipulateScript(
        typedLinesOutter,
        scriptVariant.transformation
      )

      val characterAgnosticConnectedLines: List[Either[Line, ConnectedLine]] =
        ConnectedLine.makeAllConnectedLines(manipulatedScript)
      import scalatags.Text // TODO Hrm. Doesn't belong in this file.
      val htmlOutput: List[Text.TypedTag[String]] =
        characterAgnosticConnectedLines
          .map {
            case Left(line) => Rendering.toHtml(line)
            case Right(connectedLine) =>
              Rendering.toHtml(connectedLine)
          }

      unsafeWorld.writeNewLinesForPlay(
        outputPlayName,
        scriptVariant.name,
        htmlOutput.map(_.toString)
      )

    }

    for (character <- allCharactersDynamic) {
      unsafeWorld.createCharacterDirector(character, outputPlayName)

      unsafeWorld.writeRootCharacterMenu(
        Rendering
          .characterListMenu(allCharactersDynamic, outputPlayName)
          .toString(),
        outputPlayName
      )

      val characterScripts: CharacterScripts =
        unsafeWorld.getCharacterScripts(character, outputPlayName)

      val renderedMenu =
        Rendering.characterSubdirectory(characterScripts).toString()

      unsafeWorld.writeMenu(character, renderedMenu, outputPlayName)
    }

  }

  def mitPlay(
      nameOfFileToParse: String,
      outputPlayName: String,
      scriptVariants: Set[ScriptVariant]
  ): Unit = {
    playVariations(
      nameOfFileToParse,
      outputPlayName,
      scriptVariants,
      MitHtml.typedLinesFromRawScript
    )

  }

  def romeoAndJuliet(): Unit = {
    mitPlay(
      "shakespeareGoodVersion.html",
      "romeoAndJuliet",
      ALL_SCRIPT_VARIANTS
    )
  }

  def aMidSummerNightsDream(): Unit = {
    mitPlay(
      "AMidSummerNightsDream.html",
      "AMidSummerNightsDream",
      ALL_SCRIPT_VARIANTS
    )
  }

  def muchAdoAboutNothing(): Unit = {
    mitPlay(
      "MuchAdoAboutNothing.html",
      "MuchAdoAboutNothing",
      ALL_SCRIPT_VARIANTS
    )
  }

  def hamlet(): Unit = {
    mitPlay(
      "Hamlet.html",
      "Hamlet",
      ALL_SCRIPT_VARIANTS
    )
  }

  def macbeth(): Unit = {
    mitPlay(
      "Macbeth.html",
      "Macbeth",
      ALL_SCRIPT_VARIANTS
    )
  }

  def kingLear(): Unit = {
    mitPlay(
      "KingLear.html",
      "KingLear",
      ALL_SCRIPT_VARIANTS
    )
  }

  def juliusCaesar(): Unit = {
    mitPlay(
      "JuliusCaesar.html",
      "JuliusCaesar",
      ALL_SCRIPT_VARIANTS
    )
  }

  def theTamingOfTheShrew(): Unit = {
    mitPlay(
      "TheTamingOfTheShrew.html",
      "TheTamingOfTheShrew",
      ALL_SCRIPT_VARIANTS
    )
  }

  def theComedyOfErrors(): Unit = {
    mitPlay(
      "TheComedyOfErrors.html",
      "TheComedyOfErrors",
      ALL_SCRIPT_VARIANTS
    )
  }

  def allsWellThatEndsWell(): Unit = {
    mitPlay(
      "AllsWellThatEndsWell.html",
      "AllsWellThatEndsWell",
      ALL_SCRIPT_VARIANTS
    )
  }

  def othello(): Unit = {
    mitPlay(
      "Othello.html",
      "Othello",
      ALL_SCRIPT_VARIANTS
    )
  }

}