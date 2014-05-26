package water.sparkling.demo

import water._
import water.util.Log
import water.sparkling.{TachyonFrameExtractor, FileBasedFrameExtractor, DummyFrameExtractor}

/**
 * This demo shows how to access data stored in Spark and transfer them
 * into H2O cloud.
 */
object SparklingDemo {

  /** Name of application */
  def APP_NAME = "Sparkling"

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
    val conf = extractConf(args)
    Log.info("Running demo with following configuration: " + conf)
    try {
      // Execute a demo
      run(conf)
    } catch { // only for DEBUG - see what went wrong
      case e:Throwable => e.printStackTrace(); throw e
    } finally {
      // Always shutdown H2O worker
      //Thread.sleep(3600)
      if (conf.shutdown) H2O.CLOUD.shutdown()
    }
  }

  private def run(conf: DemoConf) = {
    val demo:Demo = conf.demoName match {
      case "prostate" => ProstateDemo
      case "airlines" => AirlinesDemo
      case s => throw new IllegalArgumentException("Unknown demo name: " + s)
    }
    Log.info("Demo configuration: " + conf)
    demo.run(conf)
  }

  def extractConf(args: Array[String]): DemoConf = {
    var local:Boolean = true
    var extractor:RDDFrameExtractor = FileBasedFrameExtractor
    var shutdown:Boolean = true
    var demoName = "prostate"
    val demoRegexp = """--demo=([a-zA-Z])""".r
    args.foreach(param => {
      param match {
        case "--local"  => local = true
        case "--remote" => local = false
        case "--extractor=dummy" => extractor = DummyFrameExtractor
        case "--extractor=file"  => extractor = FileBasedFrameExtractor
        case "--extractor=tachyon" => extractor = TachyonFrameExtractor
        case "--shutdown" => shutdown = true
        case "--noshutdown" => shutdown = false
        case demoRegexp(m) => demoName = m
      }
    })
    println(demoName)
    DemoConf(demoName, local, extractor, shutdown)
  }
}

class SparklingDemo {
}

case class DemoConf(demoName:String, local:Boolean = true, extractor: RDDFrameExtractor = FileBasedFrameExtractor, shutdown:Boolean = true)