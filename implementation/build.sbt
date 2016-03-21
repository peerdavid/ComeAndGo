name := "ComeAndGo"

version := "1.0"

lazy val `comeandgo` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc , cache , ws , specs2 % Test )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

// setting a maintainer which is used for all packaging types
maintainer := "ComeAndGo"

// exposing the play ports
dockerExposedPorts in Docker := Seq(9000, 9443)