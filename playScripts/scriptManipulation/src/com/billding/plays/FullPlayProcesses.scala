package com.billding.plays

import com.billding.plays.parsing.MitHtml
import better.files.Dsl.cwd
import zio.{Fiber, Task, ZIO}
import zio.console._

object FullPlayProcesses {
  val workingDirectory = cwd
  val unsafeWorld = new UnsafeWorld(workingDirectory)

  def mainContents(): ZIO[Console, Throwable, Unit] = {
    val parallel = true

    if (parallel) {
      ZIO
        .collectAllPar(
          Set(
            aMidSummerNightsDream,
            muchAdoAboutNothing,
            hamlet,
            macbeth,
            kingLear,
            juliusCaesar,
            romeoAndJuliet,
            theTamingOfTheShrew,
            theComedyOfErrors,
            othello
          )
        )
        .flatMap(_ => unsafeWorld.listFilesInGeneratedDir())
        .map { files =>
          Rendering.createPlayMenuContent(files)
        }
        .flatMap(
          playMenuText =>
            unsafeWorld.writePlaySelectionMenu(playMenuText.toString())
        )
        .map(_ => "Sucessfully created character menu")
    } else {

      romeoAndJuliet
        .flatMap(_ => aMidSummerNightsDream)
        .flatMap(_ => muchAdoAboutNothing)
        .flatMap(_ => hamlet)
        .flatMap(_ => macbeth)
        .flatMap(_ => kingLear)
        .flatMap(_ => juliusCaesar)
        .flatMap(_ => theTamingOfTheShrew)
        .flatMap(_ => theComedyOfErrors)
        .flatMap(_ => othello)
        .flatMap(_ => unsafeWorld.listFilesInGeneratedDir())
        .map { files =>
          Rendering.createPlayMenuContent(files)
        }
        .flatMap(
          playMenuText =>
            unsafeWorld.writePlaySelectionMenu(playMenuText.toString())
        )
        .map(_ => "Sucessfully created character menu")

    }
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

              unsafeWorld.writeNewLinesForPlay(
                outputPlayName,
                scriptVariant.name,
                htmlOutput.map(_.toString)
              )
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

                                unsafeWorld
                                  .writeMenu(
                                    character,
                                    renderedMenu,
                                    outputPlayName
                                  )
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

  private val romeoAndJuliet =
    mitPlay(
      "shakespeareGoodVersion.html",
      "romeoAndJuliet",
      ALL_SCRIPT_VARIANTS
    )

  private val aMidSummerNightsDream =
    mitPlay(
      "AMidSummerNightsDream.html",
      "AMidSummerNightsDream",
      ALL_SCRIPT_VARIANTS
    )

  private val muchAdoAboutNothing =
    mitPlay(
      "MuchAdoAboutNothing.html",
      "MuchAdoAboutNothing",
      ALL_SCRIPT_VARIANTS
    )

  private val hamlet =
    mitPlay(
      "Hamlet.html",
      "Hamlet",
      ALL_SCRIPT_VARIANTS
    )

  private val macbeth =
    mitPlay(
      "Macbeth.html",
      "Macbeth",
      ALL_SCRIPT_VARIANTS
    )

  private val kingLear =
    mitPlay(
      "KingLear.html",
      "KingLear",
      ALL_SCRIPT_VARIANTS
    )

  private val juliusCaesar =
    mitPlay(
      "JuliusCaesar.html",
      "JuliusCaesar",
      ALL_SCRIPT_VARIANTS
    )

  private val theTamingOfTheShrew =
    mitPlay(
      "TheTamingOfTheShrew.html",
      "TheTamingOfTheShrew",
      ALL_SCRIPT_VARIANTS
    )

  private val theComedyOfErrors =
    mitPlay(
      "TheComedyOfErrors.html",
      "TheComedyOfErrors",
      ALL_SCRIPT_VARIANTS
    )

  private val allsWellThatEndsWell =
    mitPlay(
      "AllsWellThatEndsWell.html",
      "AllsWellThatEndsWell",
      ALL_SCRIPT_VARIANTS
    )

  val othello: ZIO[Console, Throwable, Unit] =
    mitPlay(
      "Othello.html",
      "Othello",
      ALL_SCRIPT_VARIANTS
    )

}
