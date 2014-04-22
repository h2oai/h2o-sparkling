package water.sparkling

import scala.reflect.runtime.universe.TypeTag
import org.apache.spark.rdd.RDD
import water.fvec.Frame

package object demo {
  //type FrameExtractor[S <: Product : TypeTag] = RDD[org.apache.spark.sql.Row] => Frame
  // Function to extract Frame from RDD
  trait GenericFrameExtractor[-F, +T] {
    def apply[S <: Product : TypeTag](v:F):T
  }
  type RDDFrameExtractor = GenericFrameExtractor[RDD[org.apache.spark.sql.Row], Frame]

  /* Parser is passed to Spark map call so it needs to be Serializable */
  trait Parser[S <: Product] extends Serializable {
    def apply(row: Array[String]) : S
  }
}
