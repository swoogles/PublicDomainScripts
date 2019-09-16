package com.billding.plays

import com.billding.plays.parsing.MitHtml
import better.files.Dsl.cwd
import zio.{Task, ZIO}

object FullPlayProcesses {
  val workingDirectory = cwd
  val unsafeWorld = new UnsafeWorld(workingDirectory)

  def mainContents(): Task[String] = {

    romeoAndJuliet()
      .flatMap(_ => aMidSummerNightsDream())
      .flatMap(_ => muchAdoAboutNothing())
      .flatMap(_ => hamlet())
      .flatMap(_ => macbeth())
      .flatMap(_ => kingLear())
      .flatMap(_ => juliusCaesar())
      .flatMap(_ => theTamingOfTheShrew())
      .flatMap(_ => theComedyOfErrors())
      .flatMap(_ => othello())
      .flatMap { _ =>
        ZIO { unsafeWorld.getFilesInGeneratedDir() }
      }
      .map { files =>
        Rendering.listPlays(files)
      }
      .flatMap(
        playMenuText =>
          ZIO {
            unsafeWorld.writePlaySelectionMenu(playMenuText.toString())
            "Sucessfully created character menu"
          }
      )
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
  ): List[Line] =
    lines.zipWithIndex
      .map(lineTyper.tupled)

  def getCharactersWithSpokenLines(
      typedLines: List[Line]
  ): List[PlayCharacter] =
    typedLines
      .collect {
        case manipulatedLine: ManipulatedLine => manipulatedLine.character
        case spokenLine: SpokenLine           => spokenLine.character
      }
      // Currently ignoring all group/multi-character lines
      .filter { character =>
        !(character.name.contains(",") || character.name.contains("&"))
      }
      .distinct
      .sortBy(_.name)

  def playVariations(
      nameOfFileToParse: String,
      outputPlayName: String,
      scriptVariants: Set[ScriptVariant],
      scriptTyper: String => List[Line]
  ) = {
    unsafeWorld.getFileAsOneBigString(nameOfFileToParse).flatMap {
      fileContent: String =>
        val typedLinesOutter =
          scriptTyper(fileContent)

        val allCharactersDynamic =
          getCharactersWithSpokenLines(typedLinesOutter)

        println(s"Creating script variations for $outputPlayName")
        for (scriptVariant <- scriptVariants) {
          println(s"   -${scriptVariant.name}")

          val manipulatedScript: List[Line] = Parsing.manipulateScript(
            typedLinesOutter,
            scriptVariant.transformation
          )

          val characterAgnosticConnectedLines
              : List[Either[Line, ConnectedLine]] =
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

        ZIO {
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
    }

  }

  def mitPlay(
      nameOfFileToParse: String,
      outputPlayName: String,
      scriptVariants: Set[ScriptVariant]
  ): Task[Unit] = {
    playVariations(
      nameOfFileToParse,
      outputPlayName,
      scriptVariants,
      MitHtml.typedLinesFromRawScript
    )
  }

  def romeoAndJuliet(): Task[Unit] = {
    mitPlay(
      "shakespeareGoodVersion.html",
      "romeoAndJuliet",
      ALL_SCRIPT_VARIANTS
    )
  }

  def aMidSummerNightsDream(): Task[Unit] = {
    mitPlay(
      "AMidSummerNightsDream.html",
      "AMidSummerNightsDream",
      ALL_SCRIPT_VARIANTS
    )
  }

  def muchAdoAboutNothing(): Task[Unit] = {
    mitPlay(
      "MuchAdoAboutNothing.html",
      "MuchAdoAboutNothing",
      ALL_SCRIPT_VARIANTS
    )
  }

  def hamlet(): Task[Unit] = {
    mitPlay(
      "Hamlet.html",
      "Hamlet",
      ALL_SCRIPT_VARIANTS
    )
  }

  def macbeth(): Task[Unit] = {
    mitPlay(
      "Macbeth.html",
      "Macbeth",
      ALL_SCRIPT_VARIANTS
    )
  }

  def kingLear(): Task[Unit] = {
    mitPlay(
      "KingLear.html",
      "KingLear",
      ALL_SCRIPT_VARIANTS
    )
  }

  def juliusCaesar(): Task[Unit] = {
    mitPlay(
      "JuliusCaesar.html",
      "JuliusCaesar",
      ALL_SCRIPT_VARIANTS
    )
  }

  def theTamingOfTheShrew(): Task[Unit] = {
    mitPlay(
      "TheTamingOfTheShrew.html",
      "TheTamingOfTheShrew",
      ALL_SCRIPT_VARIANTS
    )
  }

  def theComedyOfErrors(): Task[Unit] = {
    mitPlay(
      "TheComedyOfErrors.html",
      "TheComedyOfErrors",
      ALL_SCRIPT_VARIANTS
    )
  }

  def allsWellThatEndsWell(): Task[Unit] = {
    mitPlay(
      "AllsWellThatEndsWell.html",
      "AllsWellThatEndsWell",
      ALL_SCRIPT_VARIANTS
    )
  }

  def othello(): Task[Unit] = {
    mitPlay(
      "Othello.html",
      "Othello",
      ALL_SCRIPT_VARIANTS
    )
  }

}
