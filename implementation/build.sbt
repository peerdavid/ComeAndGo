name := "ComeAndGo"

version := "1.0"

lazy val `comeandgo` = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

// for authentication & authorization
def pac4j = "org.pac4j" % "play-pac4j" % "2.1.0"
def pac4j_http = "org.pac4j" % "pac4j-http" % "1.8.6"
// for password encryption
def bcrypt = "org.mindrot" % "jbcrypt" % "0.3m"
// for unit testing
def mockito = "org.mockito" % "mockito-core" % "1.9.5" % "test"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  pac4j,
  pac4j_http,
  bcrypt,
  mockito,
  specs2 % Test )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

// setting a maintainer which is used for all packaging types
maintainer := "ComeAndGo"

// exposing the play ports
dockerExposedPorts in Docker := Seq(9000, 9443)