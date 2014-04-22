package water.sparkling.demo

/**
 * Created by michal on 4/18/14.
 */
object SchemaUtils {
  def int(s:String):Int = if (s!=null && !s.isEmpty) parseInt(s) else null.asInstanceOf[Int]
  def float(s:String):Float = if (s!=null && !s.isEmpty) parseFloat(s) else null.asInstanceOf[Float]
  def parseInt(s:String):Int = {
    val r = try { s.trim().toInt } catch {
      case e:NumberFormatException => null.asInstanceOf[Int]
    }
    r
  }
  def parseFloat(s:String):Float = {
    val r = try { s.trim().toFloat } catch {
      case e:NumberFormatException => null.asInstanceOf[Float]
    }
    r
  }
}
