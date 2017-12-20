name := "tcp-proxy"
version := "0.1.0"

scalaVersion := "2.12.4"

scalacOptions ++= Seq("-Xmax-classfile-name", "127")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10"
)

mainClass in Compile := Some("TcpProxy")
