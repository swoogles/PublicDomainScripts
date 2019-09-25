package com.billding.plays

import better.files.Dsl.ls
import better.files.File
import zio.console.Console
import zio.{Task, ZIO}
import zio.console._

class UnsafeWorld(workingDirectory: File) {
  private val generatedDirectory = workingDirectory.parent / "content" / "generated"

  def createCharacterDirector(
      character: PlayCharacter,
      playName: String
  ) = {
    val snakeCasedCharacterName = character.normalizedName.upperSnakeCase

    val directory = generatedDirectory / playName / snakeCasedCharacterName
    ZIO {
      directory.createDirectories()
    }
  }

  def writeNewLinesForPlay(
      play: String,
      scriptVariantName: String,
      newLines: List[String]
  ): ZIO[Console, Throwable, Unit] = {
    val dir = generatedDirectory / play
      .replace(" ", "_")
    dir.createDirectories()
    val file = dir / s"$scriptVariantName.html"
    file.overwrite("") // Clear out file contents
    val displayName = scriptVariantName.replace("_", " ").takeWhile(_ != '.') // TODO Is this the best place for this transformation?

    writeArbitraryContents(
      file,
      displayName,
      newLines
    )
  }

  @deprecated("Use version with folderName")
  def getFileAsOneBigString(fileName: String): Task[String] = ZIO {
    val betterFile: File = workingDirectory / "publicDomainScripts" / fileName
    betterFile.contentAsString
  }

  def getFileAsOneBigString(fileName: String, folderName: String): String = {
    val betterFile
        : File = workingDirectory / "publicDomainScripts" / folderName / fileName
    betterFile.contentAsString
  }

  def listFilesInGeneratedDir(
      ) =
    putStrLn("Getting files!")
      .flatMap { _ =>
        ZIO {
          ls(generatedDirectory).toList.filterNot(_.name.contains("menu"))
        }
      }

  def getCharacterScripts(
      playCharacter: PlayCharacter,
      playName: String
  ): Task[CharacterScripts] = {
    val directory = generatedDirectory / playName
      .replace(" ", "_")

    ZIO {
      ls(directory).toList
        .filterNot(_.name.contains("menu"))
        .filter(_.name.endsWith(".html"))
    }.map(files => CharacterScripts(playCharacter, files))
  }

  private def writeArbitraryContents(
      targetFile: File,
      title: String,
      content: Seq[String]
  ): ZIO[Console, Throwable, Unit] = ZIO {

    val header = s"""+++
title = "$title"
date = "2014-04-02"
toc = true
+++
"""

    targetFile.overwrite(header)
    targetFile.appendLines(content: _*)

  }

  def writeRootCharacterMenu(
      content: String,
      playName: String
  ): ZIO[Console, Throwable, Unit] = {
    val dir = generatedDirectory / playName
    dir.createDirectories()
    writeArbitraryContents(
      dir / "character_menu.html",
      "Choose Your Character",
      Seq(content)
    )
  }

  def writeMenu(
      playCharacter: PlayCharacter,
      menuContent: String,
      playName: String
  ): ZIO[Console, Throwable, Unit] = {
    val file = generatedDirectory / playName / playCharacter.normalizedName.upperSnakeCase / "menu.html"

    writeArbitraryContents(
      file,
      s"${playCharacter.name} Scripts",
      Seq(menuContent)
    )
  }

  def writePlaySelectionMenu(
      menuContent: String
  ): ZIO[Console, Throwable, Unit] = {
    writeArbitraryContents(
      generatedDirectory / "menu.html",
      s"All Scripts",
      Seq(menuContent)
    )
  }

//  val writePlaySelectionMenuZio =
//    ZIO.fromFunction(writePlaySelectionMenu)

}
