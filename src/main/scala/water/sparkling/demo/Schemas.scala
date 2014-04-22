package water.sparkling.demo

import SchemaUtils.{int,float}

case class Prostate(id:Int, capsule:Int, age:Int, race: Int, dpros: Int, dcaps: Int, psa:Float, vol:Float, gleason:Int)

object ProstateParse extends Parser[Prostate] {
  def apply(row: Array[String]): Prostate = {
    return Prostate(int(row(0)), int(row(1)), int(row(2)), int(row(3)), int(row(4)), int(row(5)), float(row(6)), float(row(7)), int(row(8)) )
  }
}

// Hihi: Implementation restriction: case classes cannot have more than 22 parameters.
case class Airlines(year:Int,
                    month:Int,
                    dayOfMonth:Int,
                    dayOfWeek:Int,
                    //depTime:Int,
                    crsDepTime:Int,
                    //arrTime:Int,
                    crsArrTime:Int,
                    uniqueCarrier:String,
                    flightNum:Int,
                    tailNum:Int,
                    //actualElapsedTime:Int,
                    crsElapsedTime:Int,
                    //airTime:Int,
                    //arrDelay:Int,
                    //depDelay:Int,
                    origin:String,
                    dest:String,
                    distance:Int,
                    //taxiIn:Int,
                    //taxiOut:Int,
                    //cancelled:Boolean,
                    //cancellationCode:String,
                    //diverted:Boolean,
                    //carrierDelay:Int,
                    //weatherDelay:Int,
                    //nasDelay:Int,
                    //securityDelay:Int,
                    //lateAircraftDelay:Int,
                    isArrDelayed: Boolean,
                    isDepDelayed:Boolean)

