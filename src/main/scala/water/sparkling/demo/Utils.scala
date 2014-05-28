package water.sparkling.demo

object SchemaUtils {

  def int  (s:String):Option[Int]     = if (isValid(s)) parseInt(s)   else None
  def float(s:String):Option[Float]   = if (isValid(s)) parseFloat(s) else None
  def str  (s:String):Option[String]  = if (isValid(s)) Option(s)     else None
  def bool (s:String):Option[Boolean] = if (isValid(s)) parseBool(s)  else None

  def parseInt(s:String):Option[Int] =
    try { Option(s.trim().toInt) } catch {
      case e:NumberFormatException => None
    }

  def parseFloat(s:String):Option[Float] =
    try { Option(s.trim().toFloat) } catch {
      case e:NumberFormatException => None
    }

  def parseBool(s:String):Option[Boolean] = s.trim().toLowerCase match {
    case "true"|"yes" => Option(true)
    case "false"|"no" => Option(false)
    case _ => None
  }

  def isNA(s:String) = s==null || s.isEmpty || (s.trim.toLowerCase match {
    case "na" => true
    case "n/a"=> true
    case _ => false
  })

  def isValid(s:String) = !isNA(s)

  //def na[S]:S = null.asInstanceOf[S]
  //def na[S]:Option[S] = None
}
