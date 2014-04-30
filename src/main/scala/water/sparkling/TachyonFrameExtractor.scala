package water.sparkling

import water.sparkling.demo.RDDFrameExtractor
import scala.reflect.runtime.universe.TypeTag
import org.apache.spark.rdd.RDD
import water.fvec.{TachyonFileVec, Frame}
import water.sparkling.Utils._
import water.util.{FrameUtils, Log}
import tachyon.client.TachyonFS
import water.Futures

/**
 *
 */
object TachyonFrameExtractor extends RDDFrameExtractor {
  def defaultTachyonServer = "localhost:19998"
  def defaultTachyonURI    = "tachyon://" + defaultTachyonServer
  def apply[S <: Product : TypeTag](rdd: RDD[org.apache.spark.sql.Row]): Frame = {
    import scala.collection.JavaConversions._ // for conversion from List to Scala seq
    // Persist the file to Tachyon
    val fileName = "/" + Utils.rddName(rdd)
    val fileURI  = defaultTachyonURI + fileName
    // - connect to Tachyon and get clientinfo of file which we want to save
    val tachyonCLI = TachyonFS.get(defaultTachyonURI)
    tachyonCLI.delete(fileName, true)
    rdd.map(_.mkString(",")).saveAsTextFile(fileURI)
    val partCnt = rdd.partitions.length
    // We need to know size of files
    // All the files for all partitions
    val files = tachyonCLI.listStatus(fileName).filter(_.name.startsWith("part-"))
    // Load into H2O:
    // - create H2O keys for each saved file
    val fs = new Futures
    val keys = files.map(f => TachyonFileVec.make(defaultTachyonServer, f, fs))
    fs.blockForPending() // Wait till all puts are done
    // - parse files referenced by keys pointing into Tachyon
    FrameUtils.parseFrame(rddKey(rdd), keys:_* )
  }
}
