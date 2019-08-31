// Turn this project into a Scala.js project by importing these settings
enablePlugins(ScalaJSPlugin)
enablePlugins(ScalaJSBundlerPlugin)

name := "Example"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.1"

scalaJSUseMainModuleInitializer := true

testFrameworks += new TestFramework("utest.runner.Framework")


libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.7",
    "com.lihaoyi" %%% "utest" % "0.6.7" % "test, it",
    "be.doeraene" %%% "scalajs-jquery" % "0.9.5",
    "org.scala-js" %% "scalajs-env-selenium" % "1.0.0-M7"
//  "org.scala-lang.modules" %%% "scala-async" % "0.10.0"

    // https://mvnrepository.com/artifact/com.thoughtworks.future/future
//    "com.thoughtworks.future" %%% "future" % "2.0.0",
//    "com.thoughtworks.raii" %%% "asynchronous" % "2.0.0"

)

npmDependencies in Compile ++= Seq(
    "jquery" -> "2.1.3"
)



lazy val myProject = project.in(file(".")).
  enablePlugins(ScalaJSPlugin).
  // add the `it` configuration
  configs(IntegrationTest).
  // add `it` tasks
  settings(Defaults.itSettings: _*).
  // add Scala.js-specific settings and tasks to the `it` configuration
  settings(inConfig(IntegrationTest)(ScalaJSPlugin.testConfigSettings): _*)

// From a 2016 article:
// https://japgolly.blogspot.com/2016/03/scalajs-firefox-chrome-sbt.html
//  .configure(InBrowserTesting.js)
