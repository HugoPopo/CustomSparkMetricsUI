/*
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

name := "custom-spark-metrics-ui"
organization := "org.apache.spark"
version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.0"
libraryDependencies += "org.scala-lang.modules" % "scala-xml_2.11" % "1.1.0"
libraryDependencies += "javax.servlet" % "javax.servlet-api" % "4.0.0" % "provided"
libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.0-M3"
libraryDependencies += "org.eclipse.jetty" % "jetty-servlet" % "9.4.7.v20170914"
