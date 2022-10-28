
name := "CS441_Homework2"

version := "0.1"

scalaVersion := "2.13.9"


val LogbackVersion = "1.2.6"
val sfl4sVersion = "2.0.3"
val typesafeConfigVersion = "1.4.2"
val scalacticVersion = "3.2.14"
val generexVersion = "1.0.2"
val awsLambdaVersion = "1.2.1"
val awsLambdaEventsVersion = "3.11.0"
lazy val akkaHttpVersion = "10.2.10"
lazy val akkaVersion    = "2.6.18"

resolvers += Resolver.jcenterRepo

// assembly merge strategy
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
// compiles protobuf definitions into scala code

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)

libraryDependencies ++= Seq(

  // typesafe for config
  "com.typesafe" % "config" % typesafeConfigVersion,

  // scalatic for testing
  "org.scalactic" %% "scalactic" % scalacticVersion,
  "org.scalatest" %% "scalatest" % scalacticVersion % Test,
  "org.scalatest" %% "scalatest-featurespec" % scalacticVersion % Test,
  "com.github.mifmif" % "generex" % generexVersion,

  // scalabp for gRPC protobuf

  "io.grpc" % "grpc-alts" % "1.41.0",
  "io.grpc" % "grpc-protobuf" % "1.41.0",
  "io.grpc" % "grpc-stub" % "1.41.0",
  "io.netty" % "netty-tcnative-boringssl-static" % "2.0.20.Final",
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,


  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % LogbackVersion,

  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.1.4" % Test,

  "com.amazonaws" % "aws-lambda-java-core" % awsLambdaVersion,
  "com.amazonaws" % "aws-lambda-java-events" % awsLambdaEventsVersion,

  "com.amazonaws" % "aws-java-sdk-s3" % "1.12.99"

)
