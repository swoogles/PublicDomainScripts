package com.billding.plays

import better.files.File
import com.billding.plays.Rendering.idFor
import scalatags.Text
import scalatags.Text.all._

case class ConnectedLineToRender(
    previousLineId: String,
    nextLineId: String,
    index: Int,
    idForSelf: String,
    cueLineId: String,
    scrollForwardBufferId: String,
    previousBufferId: String,
    nextLineBufferId: String
)

object ConnectedLineToRender {
  def createFromConnectedLine(
      connectedLine: ConnectedLine
  ): ConnectedLineToRender = {
    ConnectedLineToRender(
      previousLineId = idFor(
        connectedLine.previousLine.index
      ),
      nextLineId = idFor(
        connectedLine.nextLine.index
      ),
      index = connectedLine.self.index,
      idForSelf = idFor(connectedLine.self.index),
      cueLineId = idFor(
        connectedLine.cueLine.map(_.index).getOrElse(connectedLine.self.index)
      ),
      scrollForwardBufferId = idFor(connectedLine.selfScrollForwardBuffer.index),
      previousBufferId = idFor(
        connectedLine.previousBuffer.index
      ),
      nextLineBufferId = idFor(
        connectedLine.nextBuffer.index
      )
    )

  }
}

object Rendering {
  def toHtml(
      connectedLine: ConnectedLine
  ): Text.TypedTag[String] = {
    val connectedLineToRender =
      ConnectedLineToRender.createFromConnectedLine(connectedLine)
    val manipulatedLine = connectedLine.self

    // TODO This cue line generation should happen later. In the extreme case, it could even go to the client. Would make a significant difference in output file size.
    val cueLineContent: String = connectedLine.cueLine
      .map(
        spokenLine => "" + spokenLine.character.name + ". " + spokenLine.content
      )
      .getOrElse("")
    div(
      `class` := s"padded ${connectedLine.self.character.normalizedName.upperSnakeCase}", // Previously applied here: targetCharacter
      id := connectedLineToRender.idForSelf,
      // TODO take note of all these variables that end with "Id". Maybe they should be a type that provides the id as a field.
      data("previous-line") := connectedLineToRender.previousLineId,
      data("previous-line-buffer") := connectedLineToRender.previousBufferId,
      data("cue-line-id") := connectedLineToRender.cueLineId,
      data("cue-line") := cueLineContent,
      data("scroll-buffer") := connectedLineToRender.scrollForwardBufferId,
      data("next-line") := connectedLineToRender.nextLineId,
      data("next-line-buffer") := connectedLineToRender.nextLineBufferId
    )(
      // TODO This should be a separate function that renders ManipulatedLine
      span(b(manipulatedLine.character.name + ". ")),
      span(display := "inline", cls := "full-content")(manipulatedLine.content),
      span(display := "none", cls := "reduced-content")(
        manipulatedLine.reducedContent
      )
    )

  }

  def idFor(lineNumber: Int): String = { // TODO privatize/move
    "script-element-" + lineNumber
  }

  def toHtml(
      line: Line
  ): Text.TypedTag[String] = {
    line match {
      case StartLine(_) => h2(id := "start")("Start")
      // TODO If *all* lines are manipulated, then I can load one script with all characters reduced-content, and check a character queryParam to decide which lines to hide. "targetCharacter" then goes away.
      case ManipulatedLine(character, spokenContent, index, reducedContent) => // Shouldn't actually be using this case anymore
        div(
          `class` := s"manipulatedLine padded ${character.normalizedName.upperSnakeCase}",
          id := idFor(index)
        )(
          span(b(character.name + ". ")),
          span(display := "none", cls := "full-content")(spokenContent),
          span(display := "inline", cls := "reduced-content")(reducedContent)
        )
      case SpokenLine(character, spokenContent, index) =>
        div(cls := "padded", id := idFor(index))(
          span(b(character.name + ". ")),
          span(spokenContent)
        )
      case CombinedSpokenLine(characters, spokenContent, index) =>
        div(cls := "padded", id := idFor(index))(
          span(b("ALL. ")),
          span(spokenContent)
        )
      case ActBreak(actName, index) =>
        div(cls := "padded", id := idFor(index))(actName)
      case SceneBreak(sceneName, index) =>
        div(cls := "padded", id := idFor(index))(sceneName)
      case StageDirection(direction, index) =>
        div(cls := "padded", id := idFor(index))(direction)
      case PageNumber(number, index) =>
        h4(cls := "padded", id := idFor(index))(number)
      case PageBreak(index) =>
        hr(cls := "padded", id := idFor(index))
      case BlankLine(index) =>
        br(cls := "padded", id := idFor(index))
      case EndLine(_) => h2(id := "end")("End")
    }
  }

  val DIRECTORY_NAME = "opensourceplaymemorization"

  def getrelativeUrl(filePath: String): String = {
    filePath.toLowerCase
//                              "/home/bfrasure/repositories/opensourceplaymemorization/content/generated/othello/character_menu"
      .replace(s"/home/bfrasure/repositories/$DIRECTORY_NAME/content", "") // TODO this is brittle
      .takeWhile(_ != '.')
  }

  private def makeAnchorFromFile(file: File) = {
    a(href := getrelativeUrl(file.canonicalPath))(
      file.nameWithoutExtension.split("_").map(_.capitalize).mkString(" ")
    )
  }

  private def makeAnchorFromFile(file: File, character: PlayCharacter) = {
    a(
      href := getrelativeUrl(file.canonicalPath) + s"?character=${character.normalizedName.upperSnakeCase}"
    )(
      file.nameWithoutExtension
        .split("_")
        .map(_.capitalize)
        .mkString(" ")
    )
  }

  // NOTE: This makes an anchor to a menu.html file *INSIDE* the file you pass in. THIS BELONGS IN A MORE ABSTRACT LAYER!!
  private def makeAnchorToMenuInsideDir(file: File) = {
    a(href := getrelativeUrl((file / "character_menu.html").canonicalPath))(
      file.nameWithoutExtension.split("_").map(_.capitalize).mkString(" ")
    )
  }

  def createPlayMenuContent(
      plays: List[File]
  ): Text.TypedTag[String] = {
    val scriptLinks =
      plays
        .map { file =>
          h3(makeAnchorToMenuInsideDir(file))
        }
    div(
      scriptLinks
    )
  }

  def characterSubdirectory(
      characterScripts: CharacterScripts
  ): Text.TypedTag[String] = {
    div(
      characterScripts.scripts
        .map { file =>
          h3(makeAnchorFromFile(file, characterScripts.playCharacter))
        }
    )
  }

  def characterListMenu(
      characters: List[PlayCharacter],
      playName: String
  ): Text.TypedTag[String] = {
    div(
      characters.map { character =>
        h3(
          a(
            href := s"/generated/${playName.toLowerCase}/${character.normalizedName.snakeCase}/menu" // TODO don't do name.replace.lowercase at this layer
          )(
            character.normalizedName.spacedUpperCase
          )
        )
      }
    )
  }

}
