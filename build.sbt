name := "tcp-proxy"
version := "1.0.0"

scalaVersion := "2.12.6"

scalacOptions ++= Seq("-Xmax-classfile-name", "127")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.12",
  "com.typesafe.akka" %% "akka-http" % "10.1.1"
)

mainClass in Compile := Some("TcpProxy")
