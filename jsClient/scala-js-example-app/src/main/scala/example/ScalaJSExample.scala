package example

import zio.{App, IO, ZIO}
import zio.console._
import org.scalajs.dom.document

object ScalaJSExample extends App {
//  def main(args: List[String]): Unit =

  /*
  def run(args: List[String]): IO[Nothing, Unit] = {
    for {
      _ <- ZIO ( ScriptNavigation.setupScriptNavigationOrHideControls() )
      _ <- ZIO { document.body }
    } yield ()
    IO.unit
  }

   */


//  /*
  override def run(args: List[String]): ZIO[Environment, Nothing, Int] = {
    ScriptNavigation.setupScriptNavigationOrHideControls()

    for {
      _ <- putStrLn("doing stuff in ZIO!")
      _ <- putStrLn("More stuff")
    } yield (1)

  }

//   */
}
