package water.sparkling.demo

import water.sparkling.DummyFrameExtractor
import water.fvec.Frame
import water.util.Log

object ProstateDemo extends Demo {
  override def run(conf: DemoConf): Unit = prostateDemo(frameExtractor=conf.extractor, sparkMaster = if (conf.local) null else conf.sparkMaster)

  def prostateDemo(frameExtractor:RDDFrameExtractor, sparkMaster:String):Unit = {
    // Specifies how data are extracted from RDD into Frame
    val fextract  = frameExtractor

    // Dataset to parse
    val dataset   = "data/prostate.csv"
    // Row parser
    val rowParser = ProstateParse
    val tableName = "prostate_table"
    val query = "SELECT * FROM prostate_table WHERE capsule=1"

    // Connect to shark cluster and make a query over prostate, transfer data into H2O
    val frame:Frame = executeSpark[Prostate](dataset, rowParser, fextract, tableName, query, sparkMaster=sparkMaster)

    Log.info("Extracted frame from Spark: ")
    Log.info(if (frame!=null) frame.toString + "\nRows: " + frame.numRows() else "<nothing>")
  }

  override def name: String = "prostate"
}
