package water.sparkling

import water.sparkling.demo.RDDFrameExtractor
import org.apache.spark.rdd.RDD
import water.fvec.Vec.VectorGroup
import water.fvec.{NewChunk, AppendableVec,Frame}
import water.{H2O, DRemoteTask}
import org.apache.spark.Partition
import scala.reflect.runtime.universe.TypeTag

/** A frame extractor which goes around H2O cloud and force
  * each node to load data from a specified part of RDD.
  *
  * NOTE: this does not work since RDD cannot be shared among multiple processes
  */
object DistributedFrameExtractor extends RDDFrameExtractor {
    def apply[S <: Product : TypeTag](rdd: RDD[org.apache.spark.sql.Row]): Frame = {
      val sc = rdd.context
      val vg = new VectorGroup()
      val vec = new AppendableVec(vg.addVec())
      val result = sc.runJob(rdd, (partIt:Iterator[org.apache.spark.sql.Row]) => {
        val a = new NewChunk(vec, 1)
        a.close(null)
        a
      })
      //new PartitionExtractor(rdd).invokeOnAllNodes()
      result.foreach(println)
      null
    }

    /**
     * Distributed task to extract a partition data into frame.
     */
    class PartitionExtractor(val rdd: RDD[org.apache.spark.sql.Row]) extends DRemoteTask[PartitionExtractor] {
      def lcompute():Unit = {
        // Connect to Spark cloud
        val sc = rdd.context
        println(sc)
        //sc.get
        // Get create RDD and query for its partition data assigned for this node

        tryComplete()
      }
      private def isMyPartition(p: Partition):Boolean = (p.index % H2O.CLOUD.size() == H2O.SELF.index())
      def reduce(drt: PartitionExtractor):Unit = {}
    }

  override def name: String = "distributed"
}
