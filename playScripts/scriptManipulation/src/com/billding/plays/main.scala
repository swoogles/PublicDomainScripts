package com.billding.plays

import zio.{App, ZIO}
import zio.console._

object main extends App {

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] = {

    myAppLogic.fold(failure => {
      println("Failure: " + failure)
      println("ouch!")
      1
    }, _ => 0)
  }

  val myAppLogic =
    for {
//      env <- ZIO.environment[Int]
      _ <- putStrLn("Let's convert some scripts!")
      _ <- FullPlayProcesses.mainContents()
      _ <- putStrLn("We converted scripts!!")
//      name <- getStrLn
//      _ <- putStrLn(s"Hello, ${name}, welcome to ZIO!")
    } yield (1)

}
