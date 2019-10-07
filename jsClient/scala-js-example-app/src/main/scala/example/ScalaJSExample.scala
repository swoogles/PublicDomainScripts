package example

import zio.{App, ZIO}
import zio.console._

object ScalaJSExample extends App {
  override def run(args: List[String]): ZIO[Environment, Nothing, Int] = {

    val logic = for {
      _ <- putStrLn("doing stuff in ZIO!")
      _ <- ScriptNavigation.setupScriptNavigationOrHideControls()
      _ <- putStrLn("More stuff")
    } yield (0)

    logic.fold(failure => {
      println("Failure: " + failure)
      println("ouch!")
      1
    }, _ => {
      println("Hey everybody, check out how good I run. any tye!")
      0
    })
  }

}
