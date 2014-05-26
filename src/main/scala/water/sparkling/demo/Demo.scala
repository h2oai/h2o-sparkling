package water.sparkling.demo

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag
import water.fvec.Frame
import org.apache.spark.sql.SQLContext
import org.apache.spark.rdd.RDD
import water.util.Log
import org.apache.spark.{SparkConf, SparkContext}

trait Demo {
  def name:String
  def run(conf: DemoConf)

  def executeSpark[S <: Product : ClassTag : TypeTag](dataset: String, rowParser: Parser[S], frameExtractor: RDDFrameExtractor, tableName: String, query: String, local: Boolean = true): Frame = {
    Log.info("Table: " + tableName)
    Log.info("Query: " + query)
    Log.info("Spark: " + (if (local) "LOCAL" else "REMOTE"))

    val sc = createSparkContext(local)
    val data = sc.textFile(dataset, 2).cache()

    // SQL query over RDD
    val sqlContext = new SQLContext(sc)
    // make visible all members of sqlContext object
    import sqlContext._
    // Dummy parsing so far :-/
    val table: RDD[S] = data.map(_.split(",")).map(row => rowParser(row))
    table.registerAsTable(tableName)

    val result = sql(query)
    Log.info("RDD result has: " + result.count() + " rows")
    val f = frameExtractor[S](result)
    sc.stop() // Will cause ThreadDeathError in Spark since DiskBlockManager is calling Thread.stop(), but this client will be already gone
    f // return value
  }

  private def createSparkContext(local:Boolean = true): SparkContext = {
    val master = if (local) "local" else "spark://localhost:7077"
    val conf = new SparkConf()
      .setMaster(master)
      .setAppName(SparklingDemo.APP_NAME+"@"+name)
      //.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      //.set("spark.kryo.registrator", "water.sparkling.KryoSerialRegistrator")
      .set("spark.executor.memory", "1g")
    if (!local) // Run 'sbt assembly to produce target/scala-2.10/h2o-sparkling-demo-assembly-1.0.jar
      conf.setJars(Seq("target/scala-2.10/h2o-sparkling-demo-assembly-1.0.jar"))

    Log.info("Creating " + (if (local) "LOCAL" else "REMOTE ("+master+")") + " Spark context." )
    new SparkContext(conf)
  }
}