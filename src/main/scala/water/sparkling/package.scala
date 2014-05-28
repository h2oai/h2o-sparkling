package water

import org.apache.spark.rdd.RDD

package object sparkling {
  implicit def toRDD2H2OFrameSerializer(rdd:RDD[org.apache.spark.sql.Row]) = new SQLRDD2H2OFrameSerializer(rdd)
}
