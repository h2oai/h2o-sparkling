name := "h2o-sparkling-demo"

version := "1.0"

scalaVersion := "2.10.3"

organization := "0xdata.com"

/* Add sonatype repo to get H2O */
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

/** Add Akka repo for Spark */
resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

/** Add cloudera repo */
resolvers += "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos"

/* Dependencies - %% appends Scala version to artifactId */
libraryDependencies += "ai.h2o" % "h2o-core" % "2.5-SNAPSHOT"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.0.0-SNAPSHOT"  // Spark-CORE: do not forget %% to select spark-core distribution reflecting Scala version

libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.0.0-SNAPSHOT"  // Spark-SQL

/* Setup run
   - Fork in run
*/
fork in run := true

connectInput in run := true

outputStrategy in run := Some(StdoutOutput)

javaOptions in run ++= Seq("-Xmx4g", "-Xms4g")

// For debugging from Eclipse
//javaOptions in run += "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=1044"


