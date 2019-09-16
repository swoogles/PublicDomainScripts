package com.billding.plays

import com.billding.plays.parsing.MitHtml
import better.files.Dsl.cwd
import zio.{Task, ZIO}
import zio.console._

object FullPlayProcesses {
  val workingDirectory = cwd
  val unsafeWorld = new UnsafeWorld(workingDirectory)

  def mainContents(): ZIO[Console, Throwable, Unit] = {

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
  ): ZIO[Console, Throwable, Unit] = {
    unsafeWorld.getFileAsOneBigString(nameOfFileToParse).flatMap {
      fileContent: String =>
        val typedLinesOutter =
          scriptTyper(fileContent)

        val allCharactersDynamic =
          getCharactersWithSpokenLines(typedLinesOutter)

        val lineWriting =
          (for (scriptVariant <- scriptVariants) yield {
            putStrLn(s"   -${scriptVariant.name}").flatMap { _ =>
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

              ZIO {
                unsafeWorld.writeNewLinesForPlay(
                  outputPlayName,
                  scriptVariant.name,
                  htmlOutput.map(_.toString)
                )
              }
            }

          }).reduce((z1, z2) => z1.flatMap(_ => z2)) // TODO There's DEFINITELY some simple way of reducing these. Is traverse the move here?
        putStrLn(s"Creating script variations for $outputPlayName")
          .flatMap(_ => lineWriting)
          .flatMap(
            ignored =>
              (for (character <- allCharactersDynamic) yield {
                unsafeWorld
                  .createCharacterDirector(character, outputPlayName)
                  .flatMap {
                    _ =>
                      unsafeWorld
                        .writeRootCharacterMenu(
                          Rendering
                            .characterListMenu(
                              allCharactersDynamic,
                              outputPlayName
                            )
                            .toString(),
                          outputPlayName
                        )
                        .flatMap(
                          _ =>
                            unsafeWorld
                              .getCharacterScripts(character, outputPlayName)
                              .flatMap { characterScripts =>
                                val renderedMenu =
                                  Rendering
                                    .characterSubdirectory(characterScripts)
                                    .toString()

                                ZIO {
                                  unsafeWorld
                                    .writeMenu(
                                      character,
                                      renderedMenu,
                                      outputPlayName
                                    )
                                }
                              }
                        )
                  }
              }).reduce((z1, z2) => z1.flatMap(_ => z2)) // TODO There's DEFINITELY some simple way of reducing these. Is traverse the move here?
          )
    }

  }

  def mitPlay(
      nameOfFileToParse: String,
      outputPlayName: String,
      scriptVariants: Set[ScriptVariant]
  ) = {
    playVariations(
      nameOfFileToParse,
      outputPlayName,
      scriptVariants,
      MitHtml.typedLinesFromRawScript
    )
  }

  private def romeoAndJuliet() = {
    mitPlay(
      "shakespeareGoodVersion.html",
      "romeoAndJuliet",
      ALL_SCRIPT_VARIANTS
    )
  }

  private def aMidSummerNightsDream() = {
    mitPlay(
      "AMidSummerNightsDream.html",
      "AMidSummerNightsDream",
      ALL_SCRIPT_VARIANTS
    )
  }

  private def muchAdoAboutNothing() = {
    mitPlay(
      "MuchAdoAboutNothing.html",
      "MuchAdoAboutNothing",
      ALL_SCRIPT_VARIANTS
    )
  }

  private def hamlet() = {
    mitPlay(
      "Hamlet.html",
      "Hamlet",
      ALL_SCRIPT_VARIANTS
    )
  }

  private def macbeth() = {
    mitPlay(
      "Macbeth.html",
      "Macbeth",
      ALL_SCRIPT_VARIANTS
    )
  }

  private def kingLear() = {
    mitPlay(
      "KingLear.html",
      "KingLear",
      ALL_SCRIPT_VARIANTS
    )
  }

  private def juliusCaesar() = {
    mitPlay(
      "JuliusCaesar.html",
      "JuliusCaesar",
      ALL_SCRIPT_VARIANTS
    )
  }

  private def theTamingOfTheShrew() = {
    mitPlay(
      "TheTamingOfTheShrew.html",
      "TheTamingOfTheShrew",
      ALL_SCRIPT_VARIANTS
    )
  }

  private def theComedyOfErrors() = {
    mitPlay(
      "TheComedyOfErrors.html",
      "TheComedyOfErrors",
      ALL_SCRIPT_VARIANTS
    )
  }

  private def allsWellThatEndsWell() = {
    mitPlay(
      "AllsWellThatEndsWell.html",
      "AllsWellThatEndsWell",
      ALL_SCRIPT_VARIANTS
    )
  }

  def othello(): ZIO[Console, Throwable, Unit] = {
    mitPlay(
      "Othello.html",
      "Othello",
      ALL_SCRIPT_VARIANTS
    )
  }

}
