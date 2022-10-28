package Timestamp

import java.time.LocalTime
import java.time.format.DateTimeFormatter


/**
 * This object is used to contain the implicit class Primitives
 * Refer to the documentation of Primitives
 */
object TimestampPrimitives {

  /**
   * This implicit class contains a set of helper functions and abstractions
   * used to handle timestamps
   * This class allows programmers to write basic timestamps operations
   * in the infix notation (example 10:00:00.000 + 20:00:00.000)
   * @param timestamp this attribute represents the timestamp represented
   *                  as a LocalTime Object
   */
  implicit class Primitives(private val timestamp: LocalTime) extends AnyVal {

    /**
     * this function sums two timestamps
     * @param timestamp this parameter represents the timestamp to sum
     * @return the sum of the two timestamps
     */
    def +(timestamp: LocalTime): LocalTime = {

      val timestampParser = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

      LocalTime.parse("00:00:00.000", timestampParser)
        .plusHours(timestamp.getHour).plusHours(this.timestamp.getHour) // sum hours
        .plusMinutes(timestamp.getMinute).plusMinutes(this.timestamp.getMinute) // sum minutes
        .plusSeconds(timestamp.getSecond).plusSeconds(this.timestamp.getSecond) // sum seconds
        .plusNanos(timestamp.getNano).plusNanos(this.timestamp.getNano) // sum nanoseconds
    }

    /**
     * this function compares two timestamps
     * @param timestamp this parameter represents the timestamp to compare
     * @return true if the first timestamp is strictly less than the second timestamp
     *         , false otherwise
     */
    def >(timestamp: LocalTime): Boolean = {

      this.timestamp.isAfter(timestamp)
    }

    /**
     * this function compares two timestamps
     *
     * @param timestamp this parameter represents the timestamp to compare
     * @return true if the first timestamp is strictly greater than the second timestamp
     *         , false otherwise
     */
    def <(timestamp: LocalTime): Boolean = {

      this.timestamp.isBefore(timestamp)
    }

    /**
     * this function compares two timestamps
     *
     * @param timestamp this parameter represents the timestamp to compare
     * @return true if the first timestamp is equal to the second timestamp
     *         , false otherwise
     */
    def ==(timestamp: LocalTime): Boolean = {

      !(this.timestamp < timestamp) && !(this.timestamp > timestamp)
    }

  }
}