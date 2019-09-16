// build.sc
import mill._
import mill.define.{Command, Task}
import mill.scalalib.scalafmt.ScalafmtModule
import scalalib._

object scriptManipulation extends ScalaModule with ScalafmtModule {
  def scalaVersion = "2.12.7"
  override def ivyDeps = Agg(
    ivy"com.github.pathikrit::better-files:3.7.1",
    ivy"io.circe::circe-core:0.10.0",
    ivy"io.circe::circe-generic:0.10.0",
    ivy"io.circe::circe-parser:0.10.0",
    ivy"com.lihaoyi::scalatags:0.6.7",
    ivy"com.lihaoyi::pprint:0.5.3",
    ivy"com.lihaoyi::fastparse:2.1.3",
    ivy"net.ruippeixotog::scala-scraper:2.1.0",
    ivy"com.google.guava:guava:28.0-jre",
    ivy"dev.zio::zio:1.0.0-RC12-1"

    )
  object test extends Tests {
    override def ivyDeps = Agg(ivy"com.lihaoyi::utest:0.6.0")

    def testFrameworks = Seq("utest.runner.Framework")
  }


  def fancyProcess() = T.command(
    reformat
      .map{
      _ => {
        // TODO Copy ScalaJs here
        // TODO run tests
        println("I should do useful stuff here!")
      }
    }
      .map{
        _ => {
          println("Doing more things here!")
        }
      }
  )

  def printShit = T {
    // Generate current scripts
    // fastOptJs in ../jsClient/scala-js-example-app
    println("eh?")
    reformat()
    val wd = os.pwd/os.up
    val clientDir = wd / "jsClient" / "scala-js-example-app"
    interp.watch(clientDir / 'src) // Not currently doing anything
    os.proc("sbt", "myProject/fastOptJS::webpack").call(cwd = clientDir)
    os.copy.over(clientDir / "target" / "scala-2.12" / "scalajs-bundler" / "main" / "example-fastopt-bundle.js", wd / "assets" / "js" / "example-fastopt-bundle.js")
    println("copied js bundle")
  }
}
