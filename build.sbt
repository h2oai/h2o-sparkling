name := "h2o-sparkling-demo"

version := "1.0"

scalaVersion := "2.10.3"

organization := "0xdata.com"

/** Add local .m2 cache */
//resolvers += Resolver.mavenLocal
/* Add sonatype repo to get H2O */
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

/** Add Akka repo for Spark */
resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

/** Add cloudera repo */
resolvers += "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos"

/* Dependencies - %% appends Scala version to artifactId */
libraryDependencies += "ai.h2o" % "h2o-core" % "2.5-SNAPSHOT" changing()

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.0.0-SNAPSHOT"  % "provided" // Spark-CORE: do not forget %% to select spark-core distribution reflecting Scala version

libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.0.0-SNAPSHOT"  % "provided"  // Spark-SQL

libraryDependencies += "org.tachyonproject" % "tachyon" % "0.4.1-thrift" % "provided" // To support inhale of data from Tachyon

// Put back compile time "provided" dependencies
run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))

/* Setup run
   - Fork in run
*/
fork in run := true

connectInput in run := true

outputStrategy in run := Some(StdoutOutput)

javaOptions in run ++= Seq("-Xmx4g", "-Xms4g", "-Djava.security.krb5.realm=", "-Djava.security.krb5.kdc=", "-Djava.security.krb5.conf=/dev/null")

// For debugging from Eclipse
//javaOptions in run += "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=1044"

// Provides a dedicated task to launch plain H2O without Spark demo BUT
// containing Spark demo jars on classpath (to access Spark classes and demo methods). 
lazy val runH2O = taskKey[Unit]("Run H2O node")

fullRunTask(runH2O, Runtime, "water.Boot")

fork in runH2O := true
