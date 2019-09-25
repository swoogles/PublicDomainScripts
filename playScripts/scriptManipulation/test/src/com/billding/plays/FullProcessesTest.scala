package com.billding.plays

import utest._
import better.files.Dsl.cwd

object FullProcessesTest extends TestSuite{
  val tests = Tests{
       FullPlayProcesses.othello()
      // TODO Get my main class figured out / synced with this test

      val workingDirectory = cwd
      val unsafeWorld = new UnsafeWorld(workingDirectory)
      val allPlays = unsafeWorld.listFilesInGeneratedDir()
      val playMenu = Rendering.createPlayMenuContent(allPlays)
      unsafeWorld.writePlaySelectionMenu(playMenu.toString())
  }
}