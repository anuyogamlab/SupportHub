name := """play-java-rest-api-example"""

version := "1.0-SNAPSHOT"

lazy val GatlingTest = config("gatling") extend Test

lazy val root = (project in file(".")).enablePlugins(PlayJava, GatlingPlugin).configs(GatlingTest)
  .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)
  .settings(
    scalaSource in GatlingTest := baseDirectory.value / "/gatling/simulation"
  )

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "com.google.cloud" %  "google-cloud-dialogflow" % "0.44.0-alpha",
  "junit" %  "junit" % "4.12" % "test",
  "com.google.truth" %  "truth" % "0.35" % "test",
  "jstl" %  "jstl" % "1.2",
  "org.scalatest"     %% "scalatest"   % "3.0.3" % Test withSources(),
  "junit"             %  "junit"       % "4.12"  % Test
)
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.16"
libraryDependencies += filters
libraryDependencies += javaJpa
libraryDependencies += "com.google.cloud.sql" % "mysql-socket-factory" % "1.0.0-beta1"


libraryDependencies += "org.hibernate" % "hibernate-entitymanager" % "5.1.0.Final"
libraryDependencies += "io.dropwizard.metrics" % "metrics-core" % "3.2.1"
libraryDependencies += "com.palominolabs.http" % "url-builder" % "1.1.0"
libraryDependencies += "net.jodah" % "failsafe" % "1.0.3"

libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.5" % Test
libraryDependencies += "io.gatling" % "gatling-test-framework" % "2.2.5" % Test
// https://mvnrepository.com/artifact/org.codehaus.plexus/plexus-compiler-javac-errorprone
libraryDependencies += "org.codehaus.plexus" % "plexus-compiler-javac-errorprone" % "2.8.4"

libraryDependencies ++= Seq(
  "com.google.errorprone" %  "error_prone_core" % "${error_prone_core}",
  "com.google.cloud" %  "google-cloud-dialogflow" % "0.44.0-alpha",
  "junit" %  "junit" % "4.12" % "test",
  "com.google.truth" %  "truth" % "0.35" % "test",
  "jstl" %  "jstl" % "1.2",
"org.scalatest"     %% "scalatest"   % "3.0.3" % Test withSources(),
"junit"             %  "junit"       % "4.12"  % Test
)
PlayKeys.externalizeResources := false

testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
scalacOptions ++= Seq( // From https://tpolecat.github.io/2017/04/25/scalac-flags.html
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
  "-language:higherKinds",             // Allow higher-kinded types
  "-language:implicitConversions",     // Allow definition of implicit functions called views
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
  //"-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  "-Xfuture"                        // Turn on future language features.\

)

// The REPL can’t cope with -Ywarn-unused:imports or -Xfatal-warnings so turn them off for the console
scalacOptions in (Compile, console) --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings")

scalacOptions in (Compile, doc) ++= baseDirectory.map {
  (bd: File) => Seq[String](
    "-sourcepath", bd.getAbsolutePath, // todo replace my-new-project with the github project name, and replace mslinn with your github id
    "-doc-source-url", "https://github.com/mslinn/my-new-project/tree/master€{FILE_PATH}.scala"
  )
}.value