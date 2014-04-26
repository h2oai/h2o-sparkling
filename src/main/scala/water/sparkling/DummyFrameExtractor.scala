package water.sparkling

import water.sparkling.demo._
import scala.reflect.runtime.universe.TypeTag
import org.apache.spark.rdd.RDD
import water.fvec.{NewChunk, AppendableVec, Frame}
import org.apache.spark.sql.catalyst.expressions.Attribute
import org.apache.spark.sql.catalyst.ScalaReflection
import water.fvec.Vec.VectorGroup
import water.Futures
import water.sparkling.Utils._
import org.apache.spark.sql.catalyst.types._

/**
 * Dummy extractor of data from RDD.
 * <p>It fetch all data locally and fill the frame</p>
 *
 * <p>So far no handling of Enums, no compression of floats</p>
 */
object DummyFrameExtractor extends RDDFrameExtractor {
  def apply[S <: Product : TypeTag](rdd: RDD[org.apache.spark.sql.Row]): Frame = {
    // Obtain schema from
    val cols: Seq[Attribute] = ScalaReflection.attributesFor[S]
    // Collect
    val names = cols.map(a => a.name)
    val types = cols.map(a => a.dataType)
    val ncol = names.length
    // Create keys for new frame in a new vector group
    val vectorKeys = new VectorGroup().addVecs(ncol)
    // Create a set of appendable vectors representing the frame
    val avecs = vectorKeys.map(key => new AppendableVec(key))
    // Create a set of new chunks for each vector
    val ncs = avecs.map(av => new NewChunk(av, 0))

    // Really dummy version to fill a frame
    // Right now we cannot fill NewChunks directly in foreach (since NewChunk is not Serializable), so we fetch
    // data to this local driver and put them into frame after.
    // Another version would be to force each node in the cluster to fetch its own
    // partition and fill its own part of frame
    val fs = new Futures
    val localData = rdd.collect()
    fillNewChunks(ncs, localData, types)
    // Close all guys
    ncs.foreach(_.close(0, fs))
    val vecs = avecs.map(av => av.close(fs))
    // Return a new frame
    val f = new Frame(rddKey(rdd), names.toArray, vecs)
    f.delete_and_lock(null).unlock(null)
    f
  }

  private def fillNewChunks(ncs: Array[NewChunk], localData: Array[org.apache.spark.sql.Row], types: Seq[DataType]) = {
    localData.foreach(row => {
      for (i <- 0 until row.length) {
        if (row.isNullAt(i))
          ncs(i).addNA()
        else {
          types(i) match {
            case ByteType    => ncs(i).addNum(row.getByte  (i), 0)
            case ShortType   => ncs(i).addNum(row.getShort (i), 0)
            case IntegerType => ncs(i).addNum(row.getInt   (i), 0)
            case LongType    => ncs(i).addNum(row.getLong  (i), 0)
            case FloatType   => ncs(i).addNum(row.getFloat (i))
            case DoubleType  => ncs(i).addNum(row.getDouble(i))
            case StringType  => ncs(i).addEnum(0) // FIXME
          }
        }
      }
    })
  }
}