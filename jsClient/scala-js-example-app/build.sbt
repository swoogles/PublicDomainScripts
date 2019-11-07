// Turn this project into a Scala.js project by importing these settings
enablePlugins(ScalaJSPlugin)
enablePlugins(ScalaJSBundlerPlugin)

name := "ScriptNavigation"

version := "0.2-SNAPSHOT"

scalaVersion := "2.12.1"

scalaJSUseMainModuleInitializer := true

testFrameworks += new TestFramework("utest.runner.Framework")

webpackConfigFile := Some(baseDirectory.value / "my.custom.webpack.config.js")

libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.7",
    "com.lihaoyi" %%% "utest" % "0.6.7" % "test, it",
    "be.doeraene" %%% "scalajs-jquery" % "0.9.5",
    "dev.zio" %%% "zio" % "1.0.0-RC13"
)

npmDependencies in Compile ++= Seq(
    "jquery" -> "2.1.3"
)

lazy val myProject = project.in(file(".")).
  enablePlugins(ScalaJSPlugin).
  configs(IntegrationTest).
  settings(Defaults.itSettings: _*).
  // add Scala.js-specific settings and tasks to the `it` configuration
  settings(inConfig(IntegrationTest)(ScalaJSPlugin.testConfigSettings): _*)

// From a 2016 article:
// https://japgolly.blogspot.com/2016/03/scalajs-firefox-chrome-sbt.html
//  .configure(InBrowserTesting.js)

