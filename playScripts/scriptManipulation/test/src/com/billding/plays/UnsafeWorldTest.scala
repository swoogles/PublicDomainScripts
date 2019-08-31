package com.billding.plays

import utest._
import better.files.Dsl._
import better.files.File

object UnsafeWorldTest extends TestSuite{
  override def tests = Tests {
    val targetCharacter = PlayCharacter("speaker")
    val otherCharacter = PlayCharacter("otherCharacter")
//    'GetAllCharacterDirectories - {
//      UnsafeWorld.getCharacterDirectories(cwd.parent / "content" / "generated")
//    }
    // val FREDDY = PlayCharacter("FREDDY")
    // 'GetScriptsForCharacter - {
    //     val characterScripts = UnsafeWorld.getCharacterScripts(CHARACTER_GOES_HERE)
    //   val renderedMenu = Rendering.characterSubdirectory(characterScripts).toString()
    // }
  }

}
