addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.28")
addSbtPlugin("org.jetbrains" % "sbt-ide-settings" % "1.1.0")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.10")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.3")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.11"