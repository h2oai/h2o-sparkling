package water.sparkling.demo

object SchemaUtils {
  def int(s:String):Int      = if (s!=null && !s.isEmpty) parseInt(s) else na[Int]
  def float(s:String):Float  = if (s!=null && !s.isEmpty) parseFloat(s) else na[Float]
  def str(s:String):String   = if (s!=null && !s.isEmpty) s else na[String]
  def bool(s:String):Boolean = if (s!=null && !s.isEmpty) parseBool(s) else na[Boolean]

  def parseInt(s:String):Int = {
    val r = try { s.trim().toInt } catch {
      case e:NumberFormatException => na[Int]
    }
    r
  }
  def parseFloat(s:String):Float = {
    val r = try { s.trim().toFloat } catch {
      case e:NumberFormatException => na[Float]
    }
    r
  }
  def parseBool(s:String):Boolean = s.trim().toLowerCase match {
    case "true" => true
    case "yes" => true
    case "false" => false
    case "no" => false
    case _ => na[Boolean]
  }

  def na[S]:S = null.asInstanceOf[S]
}
