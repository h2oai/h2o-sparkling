package water.sparkling

import org.apache.spark.rdd.RDD
import water.Key
import java.io.File


object Utils {
  def rddKey(rdd: RDD[_], name: String): Key = Key.make("rdd_"+name+"_"+rdd.id)
  def tmpFile(rdd: RDD[_]): File = {
    val f = new File(new File(System.getProperty("java.io.tmpdir")), rddName(rdd) )
    if (f.exists()) rm(f)
    f
  }
  def rddName(rdd:RDD[_]):String = "rdd_"+rdd.name+"_"+rdd.id+".csv"
  def partFile(f:File, cnt:Int=1): Array[File] = Array.tabulate(cnt)(idx => new File(f, "part-0000"+idx))

  /** Recursive delete */
  def rm(file: File) {
    if(file == null) {
      return
    } else if(file.isDirectory) {
      val files = file.listFiles()
      if(files != null) {
        for(f <- files)
          rm(f)
      }
      file.delete()
    } else {
      file.delete()
    }
  }
}
