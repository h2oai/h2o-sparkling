package water.sparkling.demo

import SchemaUtils.{int,float,bool,str}
import java.util

case class Prostate(id      :Option[Int],
                    capsule :Option[Int],
                    age     :Option[Int],
                    race    :Option[Int],
                    dpros   :Option[Int],
                    dcaps   :Option[Int],
                    psa     :Option[Float],
                    vol     :Option[Float],
                    gleason :Option[Int])

object ProstateParse extends Parser[Prostate] {
  def apply(row: Array[String]): Prostate = {
    return Prostate(int(row(0)), int(row(1)), int(row(2)), int(row(3)), int(row(4)), int(row(5)), float(row(6)), float(row(7)), int(row(8)) )
  }
}

class Airlines( year          :Option[Int],    // 0
                month         :Option[Int],    // 1
                dayOfMonth    :Option[Int],    // 2
                dayOfWeek     :Option[Int],    // 3
                crsDepTime    :Option[Int],    // 5
                crsArrTime    :Option[Int],    // 7
                uniqueCarrier :Option[String], // 8
                flightNum     :Option[Int],    // 9
                tailNum       :Option[Int],    // 10
                crsElapsedTime:Option[Int],    // 12
                origin        :Option[String], // 16
                dest          :Option[String], // 17
                distance      :Option[Int],    // 18
                isArrDelayed  :Option[Boolean],// 29
                isDepDelayed  :Option[Boolean] // 30
                ) extends Product {

  @throws(classOf[IndexOutOfBoundsException])
  override def productElement(n: Int) = n match {
      case 0 => year
      case 1 => month
      case 2 => dayOfMonth
      case 3 => dayOfWeek
      case 4 => crsDepTime
      case 5 => crsArrTime
      case 6 => uniqueCarrier
      case 7 => flightNum
      case 8 => tailNum
      case 9 => crsElapsedTime
      case 10 => origin
      case 11 => dest
      case 12 => distance
      case 13 => isArrDelayed
      case 14 => isDepDelayed
      case _ => throw new IndexOutOfBoundsException(n.toString())
  }

  override def productArity: Int = 15

  override def canEqual(that: Any): Boolean = that.isInstanceOf[Airlines]
}

object AirlinesParser extends Parser[Airlines] {
  override def apply(r: Array[String]) = {
    val v = new Airlines(
      int(r(0)), int(r(1)), int(r(2)), int(r(3)),
      int(r(5)), int(r(7)), str(r(8)), int(r(9)),
      int(r(10)), int(r(12)), str(r(16)), str(r(17)),
      int(r(18)), bool(r(29)), bool(r(30))
    )
    v
  }
}


