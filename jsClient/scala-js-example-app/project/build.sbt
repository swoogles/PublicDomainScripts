addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.29")
//addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.15.0") // THIS was the POS that was forcing scalajs 1.0.0, despite the previous plugin!
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.15.0-0.6")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.0.4")
