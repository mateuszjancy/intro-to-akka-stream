name := "akka-http-stub"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= {
  val akka = "2.4.16"
  val akkaHttp = "10.0.1"
  Seq(
    //Akka
    "com.typesafe.akka" %% "akka-actor" % akka,
    "com.typesafe.akka" %% "akka-testkit" % akka,
    //Stream
    "com.typesafe.akka" %% "akka-stream" % akka,
    "com.typesafe.akka" %% "akka-stream-testkit" % akka,
    //Akka HTTP
    "com.typesafe.akka" %% "akka-http-core" % akkaHttp,
    "com.typesafe.akka" %% "akka-http" % akkaHttp,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttp,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttp,
    "com.typesafe.akka" %% "akka-http-jackson" % akkaHttp,
    "com.typesafe.akka" %% "akka-http-xml" % akkaHttp,
    //Tests
    "org.mockito" % "mockito-all" % "1.10.19",
    "org.scalatest" %% "scalatest" % "3.0.1"
  )
}