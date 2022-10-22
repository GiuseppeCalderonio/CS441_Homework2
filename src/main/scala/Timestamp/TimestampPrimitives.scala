package Timestamp

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object TimestampPrimitives {

  implicit class Primitives(private val timestamp: LocalTime) extends AnyVal {


    def +(timestamp: LocalTime): LocalTime = {

      val timestampParser = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

      LocalTime.parse("00:00:00.000", timestampParser)
        .plusHours(timestamp.getHour).plusHours(this.timestamp.getHour)
        .plusMinutes(timestamp.getMinute).plusMinutes(this.timestamp.getMinute)
        .plusSeconds(timestamp.getSecond).plusSeconds(this.timestamp.getSecond)
        .plusNanos(timestamp.getNano).plusNanos(this.timestamp.getNano)
    }


    def >(timestamp: LocalTime): Boolean = {

      this.timestamp.isAfter(timestamp)
    }


    def <(timestamp: LocalTime): Boolean = {

      this.timestamp.isBefore(timestamp)
    }

    def ==(timestamp: LocalTime): Boolean = {

      !(this.timestamp < timestamp) && !(this.timestamp > timestamp)
    }


  }
}