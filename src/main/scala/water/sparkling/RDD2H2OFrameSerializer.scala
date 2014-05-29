package water.sparkling

import org.apache.spark.rdd.RDD
import java.io.File

/**
 * Allows for saving RDD as H2O frame.
 */
class SQLRDD2H2OFrameSerializer(val rdd:RDD[org.apache.spark.sql.Row]) extends Serializable {
  def saveAsH2OFrameFile(f:File):Unit = saveAsH2OFrameFile(f.getAbsolutePath)
  def saveAsH2OFrameFile(path:String):Unit = {
    rdd.map(rowToString(_)).saveAsTextFile(path)
  }

  def rowToString(row:org.apache.spark.sql.Row):String = {
    var first = true;
    val sb:StringBuilder = new StringBuilder
    for (x <- row) {
      if (!first) sb.append(',') else first = false
      sb.append(if (x!=null) x else "")
    }
    sb.toString()
  }
}
