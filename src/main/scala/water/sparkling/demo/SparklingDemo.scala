package water.sparkling.demo

import water._
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.SQLContext
import water.fvec.Frame
import org.apache.spark.rdd.RDD
import scala.reflect.runtime.universe.TypeTag
import scala.Product
import scala.reflect.ClassTag
import water.util.Log
import water.sparkling.{TachyonFrameExtractor, FileBasedFrameExtractor, DummyFrameExtractor}

/**
 * This demo shows how to access data stored in Spark and transfer them
 * into H2O cloud.
 */
object SparklingDemo {

  /** Name of application */
  def APP_NAME = "Sparkling Demo"

  /** Main launch H2O bootloader and then switch to H2O classloader, and redirects the execution
    * to given class.
    */
  def main(args: Array[String]):Unit = {
    Boot.main(classOf[SparklingDemo], args)
  }

  def userMain(args: Array[String]):Unit = {
    // Now we are in H2O classloader, hurray!
    // So serve a glass of water from Spark RDD
    H2O.main(args)
    try {
      // Execute a simple demo
      prostateDemo(frameExtractor = TachyonFrameExtractor, local=true)
    } catch { // only for DEBUG - see what went wrong
      case e:Throwable => e.printStackTrace(); throw e
    } finally {
      // Always shutdown H2O worker
      //Thread.sleep(3600)
      H2O.CLOUD.shutdown()
    }
  }

  def prostateDemo(frameExtractor:RDDFrameExtractor = DummyFrameExtractor, local:Boolean = true):Unit = {
    // Specifies how data are extracted from RDD into Frame
    val fextract  = frameExtractor

    // Dataset to parse
    val dataset   = "/Users/michal/Devel/projects/h2o/repos/NEW.h2o.github/smalldata/logreg/prostate.csv"
    // Row parser
    val rowParser = ProstateParse
    val tableName = "prostate_table"
    // query for all tumor penetration of prostate capsule, i.e., capsule=1
    val query = "SELECT * FROM prostate_table WHERE capsule=1"

    // Connect to shark cluster and make a query over prostate, transfer data into H2O
    val frame:Frame = executeSpark[Prostate](dataset, rowParser, fextract, tableName, query, local=local)

    println("Extracted frame from Spark:")
    println(if (frame!=null) frame.toStringAll else "<nothing>")

  }

  def executeSpark[S <: Product : ClassTag : TypeTag](dataset: String, rowParser: Parser[S], frameExtractor: RDDFrameExtractor, tableName:String, query:String, local:Boolean = true):Frame = {
    val sc = createSparkContext(local)
    val data = sc.textFile(dataset,2).cache()

    // SQL query over RDD
    val sqlContext = new SQLContext(sc)
    // make visible all members of sqlContext object
    import sqlContext._
    // Dummy parsing so far :-/
    val table:RDD[S] = data.map(_.split(",")).map(row => rowParser(row))
    table.registerAsTable(tableName)

    val result = sql(query)
    val f = frameExtractor[S](result)
    sc.stop() // Will cause ThreadDeathError in Spark since DiskBlockManager is calling Thread.stop(), but this client will be already gone
    f // return value
  }

  private def createSparkContext(local:Boolean = true): SparkContext = {
    val master = if (local) "local" else "spark://localhost:7077"
    val conf = new SparkConf()
      .setMaster(master)
      .setAppName(APP_NAME)
      //.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      //.set("spark.kryo.registrator", "water.sparkling.KryoSerialRegistrator")
      .set("spark.executor.memory", "1g")
    if (!local) // Run 'sbt assembly to produce target/scala-2.10/h2o-sparkling-demo-assembly-1.0.jar
      conf.setJars(Seq("target/scala-2.10/h2o-sparkling-demo-assembly-1.0.jar"))

    Log.info("Creating " + (if (local) "LOCAL" else "REMOTE ("+master+")") + " Spark context." )
    new SparkContext(conf)
  }
}

class SparklingDemo {
}
