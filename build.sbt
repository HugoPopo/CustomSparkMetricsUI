name := "custom-spark-metrics-ui"
organization := "org.apache.spark"
version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.0"
libraryDependencies += "org.scala-lang.modules" % "scala-xml_2.11" % "1.1.0"
libraryDependencies += "javax.servlet" % "javax.servlet-api" % "4.0.0" % "provided"
libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.0-M3"
libraryDependencies += "org.eclipse.jetty" % "jetty-servlet" % "9.4.7.v20170914"
