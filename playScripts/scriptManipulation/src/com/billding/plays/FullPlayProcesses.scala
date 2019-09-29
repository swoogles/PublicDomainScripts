package com.billding.plays

import com.billding.plays.parsing.MitHtml
import better.files.Dsl.cwd
import better.files.File
import zio.ZIO
import zio.console._

object FullPlayProcesses {
  private val workingDirectory = cwd
  private val unsafeWorld = new UnsafeWorld(workingDirectory)

  def mainContents(): ZIO[Console, Throwable, String] = {
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
        .flatMap(files => createAndWritePlaySelectionPage(files))
        .map(_ => "Sucessfully created character menu")
    } else {

      for {
        _ <- romeoAndJuliet
        _ <- aMidSummerNightsDream
        _ <- muchAdoAboutNothing
        _ <- hamlet
        _ <- macbeth
        _ <- kingLear
        _ <- juliusCaesar
        _ <- theTamingOfTheShrew
        _ <- theComedyOfErrors
        _ <- othello
        files <- unsafeWorld.listFilesInGeneratedDir()
        _ <- createAndWritePlaySelectionPage(files)
        _ <- ZIO("Sucessfully created character menu")
      } yield { "Sucessfully created character menu" }

    }
  }

  private def createAndWritePlaySelectionPage(files: List[File]) = {
    val playMenuText = Rendering.createPlayMenuContent(files)
    unsafeWorld.writePlaySelectionMenu(playMenuText.toString())
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
  ): ZIO[Console, Throwable, List[Unit]] = {
    unsafeWorld.getFileAsOneBigString(nameOfFileToParse).flatMap {
      fileContent: String =>
        val typedLinesOutter: List[Line] =
          scriptTyper(fileContent)

        val allCharactersDynamic =
          getCharactersWithSpokenLines(typedLinesOutter)

        val lineWriting: Set[ZIO[Console, Throwable, Unit]] =
          for (scriptVariant <- scriptVariants) yield {
            putStrLn(s"   -${scriptVariant.name}")
              .flatMap { _ =>
                writeAScriptVariant(
                  outputPlayName,
                  scriptVariant,
                  typedLinesOutter
                )
              }
          }

        val characterSpecificWork: List[ZIO[Console, Throwable, Unit]] =
          for (character <- allCharactersDynamic) yield {
            characterSpecificLogic(
              outputPlayName,
              character,
              allCharactersDynamic
            )
          }

        putStrLn(s"Creating script variations for $outputPlayName")
          .flatMap(_ => ZIO.collectAll(lineWriting))
          .flatMap(ignored => ZIO.collectAll(characterSpecificWork))
    }

  }

  private def writeAScriptVariant(
      outputPlayName: String,
      scriptVariant: ScriptVariant,
      typedLinesOutter: List[Line]
  ) = {

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

  private def characterSpecificLogic(
      outputPlayName: String,
      character: PlayCharacter,
      allCharactersDynamic: List[PlayCharacter]
  ) = {
    unsafeWorld
      .createCharacterDirectory(character, outputPlayName)
      .flatMap(
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
      )
      .flatMap(
        _ =>
          unsafeWorld
            .getCharacterScripts(character, outputPlayName)
      )
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
  }

  private def mitPlay(nameOfFileToParse: String, outputPlayName: String) = {
    playVariations(
      nameOfFileToParse,
      outputPlayName,
      ALL_SCRIPT_VARIANTS,
      MitHtml.typedLinesFromRawScript
    )
  }

  private val romeoAndJuliet =
    mitPlay("shakespeareGoodVersion.html", "romeoAndJuliet")

  private val aMidSummerNightsDream =
    mitPlay("AMidSummerNightsDream.html", "AMidSummerNightsDream")

  private val muchAdoAboutNothing =
    mitPlay("MuchAdoAboutNothing.html", "MuchAdoAboutNothing")

  private val hamlet =
    mitPlay("Hamlet.html", "Hamlet")

  private val macbeth =
    mitPlay("Macbeth.html", "Macbeth")

  private val kingLear =
    mitPlay("KingLear.html", "KingLear")

  private val juliusCaesar =
    mitPlay("JuliusCaesar.html", "JuliusCaesar")

  private val theTamingOfTheShrew =
    mitPlay("TheTamingOfTheShrew.html", "TheTamingOfTheShrew")

  private val theComedyOfErrors =
    mitPlay("TheComedyOfErrors.html", "TheComedyOfErrors")

  private val allsWellThatEndsWell =
    mitPlay("AllsWellThatEndsWell.html", "AllsWellThatEndsWell")

  val othello: ZIO[Console, Throwable, List[Unit]] =
    mitPlay("Othello.html", "Othello")

}
