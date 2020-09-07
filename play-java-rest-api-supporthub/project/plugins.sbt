// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.18")
dependencyOverrides += "org.scala-sbt" % "sbt" % "0.13.15"
// Load testing tool:
// http://gatling.io/docs/2.2.2/extensions/sbt_plugin.html
addSbtPlugin("io.gatling" % "gatling-sbt" % "2.2.1")
