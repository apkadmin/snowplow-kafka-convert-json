name := "kafkaconnect"

version := "0.1"

scalaVersion := "2.13.4"
libraryDependencies += "org.apache.kafka" %% "kafka" % "2.6.0"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.12.0-rc2"
libraryDependencies += "com.snowplowanalytics" %% "snowplow-scala-analytics-sdk" % "2.1.0-M9"