package water.sparkling

import org.apache.spark.rdd.RDD
import java.io.File

/**
 * Allows for saving RDD as H2O frame.
 */
class SQLRDD2H2OFrameSerializer(val rdd:RDD[org.apache.spark.sql.Row]) extends Serializable {
  def saveAsH2OFrameFile(f:String):Unit = saveAsH2OFrameFile(new File(f))
  def saveAsH2OFrameFile(f:File):Unit = {
    rdd.map(rowToString(_)).saveAsTextFile(f.getAbsolutePath)
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
