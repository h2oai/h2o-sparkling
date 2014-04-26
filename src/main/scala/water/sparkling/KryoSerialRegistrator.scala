package water.sparkling

import org.apache.spark.serializer.KryoRegistrator
import com.esotericsoftware.kryo.Kryo
import water.fvec.{AppendableVec, Chunk, Vec, NewChunk}
import water.Key

/** Register H2O classes for serialization with Kryo
  *
  * WIP: FIXME use generated serialization/deserialization
  */
class KryoSerialRegistrator extends KryoRegistrator {

  def registerClasses(kryo: Kryo): Unit = {
    kryo.register(classOf[Key])
    kryo.register(classOf[AppendableVec])
    kryo.register(classOf[Vec])
    kryo.register(classOf[Chunk])
    kryo.register(classOf[NewChunk])
  }
}
