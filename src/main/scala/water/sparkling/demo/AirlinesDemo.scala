package water.sparkling.demo

import water.fvec.Frame

object AirlinesDemo extends Demo {
  override def run(conf: DemoConf): Unit = {
    //val dataset   = "data/allyears2k_headers.csv" // Dataset
    val dataset   = "data/allyears2k_headers.csv" // Dataset
    val rowParser = AirlinesParser // Row parser
    val tableName = "airlines_table" // Table name for SQL
    val query = """SELECT * FROM airlines_table WHERE dest="SFO" """
    //val query = """SELECT * FROM airlines_table WHERE dest IS NULL """
    //val query = """SELECT * FROM airlines_table WHERE year IS NULL"""
    // Connect to shark cluster and make a query over prostate, transfer data into H2O
    val frame:Frame = executeSpark[Airlines](dataset, rowParser, conf.extractor, tableName, query, local=conf.local)
    // Now call GLM
  }

  override def name: String = "airlines"
}
