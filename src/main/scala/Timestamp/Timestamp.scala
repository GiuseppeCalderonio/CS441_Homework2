package Timestamp

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}
import HelperUtils._
import TimestampPrimitives.Primitives

object Timestamp {

  private val timestampParser = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

  private val timeRegex = new Regex(Parameters.timeRegexp)

  /**
   * this value is used for logging purposes
   */
  private val logger = CreateLogger(classOf[TimestampPrimitives.type])

  def getTimestamp(stringTimestamp : String): LocalTime = {
    Try(LocalTime.parse(stringTimestamp, timestampParser)) match{
      // not parsed correctly
      case Failure(exception) =>
        logger.error(s"The string cannot be parsed as a timestamp, should be in the format: HH:mm:ss.SSS ")
        throw new IllegalArgumentException(exception)
      // parsed correctly
      case Success(value) =>
        logger.info(s"Timestamp $stringTimestamp created")
        value
    }
  }


  def toString(timestamp : LocalTime): String = {

    timestamp.format(timestampParser)
  }


  private def parseLine(line: String): LocalTime = {
    timeRegex.findFirstIn(line) match {
      case Some(value) => getTimestamp(value)
      case None => logger.error("String in log file does not contain a timestamp")
        throw new IllegalStateException("Log file does not contain correct log lines")
    }


  }

  // returns the index of the closest interval if exists, -1 otherwise
  def findTimestamp(arr: Array[String], timestamp: LocalTime, extreme: Int): Int ={

    // if the timestamp does not fit in the array
    if (parseLine(arr.head) > timestamp ||
    parseLine(arr.last) < timestamp)  {
      logger.warn(s"Timestamp ${timestamp.toString} not found in the input array")
      return -1
    }
    logger.info("Start binary search")
    recursiveBinarySearch(arr, timestamp, 0, arr.length - 1) + extreme // if I search lower extreme then extreme =0, then extreme = 1

  }

  private def recursiveBinarySearch(arr: Array[String],
                                    timestamp: LocalTime,
                                    low: Int,
                                    high: Int): Int = {


    // search is finished
    if (low == high + 1) {
      logger.debug(s"Result found for timestamp ${timestamp.toString} at line $low, line is" +
        s" ${arr(low)} (or ${low + 1} , timestamp is ${arr(low + 1)})")
      return low
    }


    // Getting the middle element
    val middle = low + (high - low) / 2

    // If element is exactly equal to timestamp

    if (parseLine(arr(middle)) == timestamp) {
      logger.debug(s"Exact result found for timestamp ${timestamp.toString} at line $middle, line is ${arr(middle)}")
      middle
    }
    // Searches in the left half
    else if (parseLine(arr(middle)) > timestamp) {
      logger.debug(s"Search in subArray[$low, ${middle - 1}]")
      recursiveBinarySearch(arr, timestamp, low, middle - 1)
    }
    // Searches in the right half
    else {
      logger.debug(s"Search in subArray[${middle + 1}, $high]")
      recursiveBinarySearch(arr, timestamp, middle + 1, high)
    }

  }


  def filterForPattern(pattern : Regex, logMessages : Array[String]): Array[String] = {
    logger.info("Searching for regex pattern in log lines")
    logMessages.filterNot(pattern.findFirstIn(_).isEmpty)
  }



}
