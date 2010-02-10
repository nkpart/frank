import sbt._

class FrankProject(info: ProjectInfo) extends ParentProject(info) {
  lazy val core = project("core", "Frank", new FrankCore(_))

  lazy val examples = project("examples", "Frank Examples", new FrankExamples(_), core)

  class FrankCore(info: ProjectInfo) extends DefaultProject(info) {
    val snapshots = "scala-tools snapshots" at "http://www.scala-tools.org/repo-snapshots"
    val scalatest = "org.scalatest" % "scalatest" % "1.0.1-for-scala-2.8.0.Beta1-RC7-with-test-interfaces-0.3-SNAPSHOT" % "test"

    val scalaz_core = "com.googlecode.scalaz" % "scalaz-core_2.8.0.Beta1-RC8" % "5.0-SNAPSHOT"
    val scalaz_http = "com.googlecode.scalaz" % "scalaz-http_2.8.0.Beta1-RC8" % "5.0-SNAPSHOT"

    val belt = "prohax" %% "belt" % "0.1"
  }
  
  class FrankExamples(info: ProjectInfo) extends DefaultWebProject(info) {
    val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.14" % "test"  // jetty is only need for testing
  }
}

