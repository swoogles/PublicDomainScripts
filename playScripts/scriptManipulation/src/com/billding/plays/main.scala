package com.billding.plays

import zio.App
import zio.console._

object main extends App {
  def run(args: List[String]) = {

    myAppLogic.fold(_ => 1, _ => 0)
  }

  val myAppLogic =
    for {
      _ <- putStrLn("Let's convert some scripts!")
      result <- zio.IO { FullPlayProcesses.mainContents() }
      _ <- putStrLn("We converted scripts!!")
//      name <- getStrLn
//      _ <- putStrLn(s"Hello, ${name}, welcome to ZIO!")
    } yield ()

}
