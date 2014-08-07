package water.sparkling.demo

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag
import water.fvec.Frame
import org.apache.spark.sql.SQLContext
import org.apache.spark.rdd.RDD
import water.util.Log
import org.apache.spark.{SparkConf, SparkContext}
import java.util

trait Demo {
  def name:String
  def run(conf: DemoConf)

  def executeSpark[S <: Product : ClassTag : TypeTag](dataset: String, rowParser: Parser[S], frameExtractor: RDDFrameExtractor, tableName: String, query: String, local: Boolean = true, hasHeader: Boolean = true): Frame = {
    Log.info("Data : " + dataset)
    Log.info("Table: " + tableName)
    Log.info("Query: " + query)
    Log.info("Spark: " + (if (local) "LOCAL" else "REMOTE"))

    val sc = createSparkContext(local)
    val data = sc.textFile(dataset,2).cache()

    // SQL query over RDD
    val sqlContext = new SQLContext(sc)
    // make visible all members of sqlContext object
    import sqlContext._
    // Dummy parsing
    val table: RDD[S] = data
                          // FIXME: skip header .mapPartitionsWithIndex((partIdx:Int,lines:Iterator[String]) => { if (partIdx==0 && hasHeader && lines.length>0) lines.drop(1) else lines })
                          .map(_.split(","))
                          .map(row => rowParser(row))
    table.registerAsTable(tableName)
    // Make a query over the table
    val result = sql(query)
    Log.info("RDD result has: " + result.count() + " rows")
    result.setName(tableName) // assign a name to resulting RDD
    val f = frameExtractor[S](result)
    sc.stop() // Will cause ThreadDeathError in Spark since DiskBlockManager is calling Thread.stop(), but this client will be already gone
    // Setup headers based on Schema
    if (hasHeader) {
      f.write_lock(null)
      try {
        val tt = implicitly[ClassTag[S]]
        val names = tt.runtimeClass.getDeclaredFields().map(_.getName)
        f._names = names
        f.update(null)
      } finally f.unlock(null)
    }
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