package example

import java.io

import org.scalajs.dom
import org.scalajs.dom.raw.Window
import org.scalajs.jquery.JQueryStatic
import zio.clock.Clock
import zio.{App, ZIO}
import zio.console._
import zio.random.Random
import zio.system.System

class BillEnvironment (
                             val window: Window,
                             jQueryStatic: JQueryStatic,
                             val console: Console.Service[Any]
                           ) extends WindowProvider with JQueryProvider with Console {
  def getWindow: Window = window
  def getJqueryObject: JQueryStatic = {
    println("Pulling this jquery object out of the Environmen!!")
    jQueryStatic
  }
}

trait WindowProvider {
  def getWindow: Window
}



trait JQueryProvider {
  def getJqueryObject: JQueryStatic
}

object ScalaJSExample extends App {


  override def run(args: List[String]): ZIO[Environment, Nothing, Int] = {
    val liveDefaultEnvironment: Environment =
      new Clock.Live with Console.Live with System.Live with Random.Live
    //     = Environment.console
    val liveEnvironment =
      new BillEnvironment(
        dom.window,
        jquery,
        Environment.console
      )

    val logic: ZIO[Console with JQueryProvider, io.Serializable, Int] = for {
      _ <- putStrLn("doing stuff in ZIO!")
      url <- ZIO { dom.window.location.toString }
      _ <- ScriptNavigation.setupScriptNavigationOrHideControls()
      _ <- ScriptNavigation.trimDownScriptIfQueryParameterIsPresent(url)
      _ <- putStrLn("More stuff")
    } yield (0)
    logic.provide(liveEnvironment)
      .fold(failure => {
        println("Failure: " + failure)
        println("ouch!")
        1
      }, _ => {
        println("Hey everybody, check out how good I run. any tye!")
        0
      })
  }

}
