package water.sparkling

import water.sparkling.demo._

import org.apache.spark.rdd.RDD
import water.fvec.Frame
import water.util.{FrameUtils, Log}
import scala.reflect.runtime.universe.TypeTag
import Utils._

object FileBasedFrameExtractor extends RDDFrameExtractor {
  def apply[S <: Product : TypeTag](rdd:RDD[org.apache.spark.sql.Row]): Frame = {
    val f = tmpFile(rdd)
    Log.info("Going to write RDD into " + f.getAbsoluteFile)
    // This is dummy write of single file which reduce all partitions
    // into 1 <- FIXME
    val partCnt = rdd.partitions.length
    rdd.saveAsH2OFrameFile(f) // Merge all-partitions into one and save it as one file
    FrameUtils.parseFrame(rddKey(rdd), partFile(f,partCnt):_* )
  }

  override def name: String = "file"
}