package Timestamp

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}
import HelperUtils._
import TimestampPrimitives.Primitives

import scala.annotation.tailrec

/**
 * This object contains a set of helper functions to handle timestamp operations
 * like creation from string, binary search on a log file, filter in a set of messages, etc...
 */
object Timestamp {

  /**
   * this attribute represents the parser for the timestamp with pattern "HH:mm:ss.SSS"
   */
  private val timestampParser = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

  /**
   * this attribute represents the regular expression that generates a timestamp
   * in the form "HH:mm:ss.SSS"
   */
  private val timeRegex = new Regex(Parameters.timeRegexp)

  /**
   * this value is used for logging purposes
   */
  private val logger = CreateLogger(classOf[Timestamp.type])

  /**
   * this function is used to get a LocalTime object given a string
   * @param stringTimestamp this parameter represents the string to convert into a timestamp
   * @return the LocalTime object if the string is a valid timestamp with
   *         the pattern "HH:mm:ss.SSS", throws an exception otherwise
   */
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

  /**
   * this function returns a string given a timestamp
   * @param timestamp this parameter represents the timestamp to convert
   * @return the string associated with the timestamp
   */
  def toString(timestamp : LocalTime): String = {

    timestamp.format(timestampParser)
  }

  /**
   *  this function is used to parse a log message line and extract a timestamp from it
   * @param line this parameter represents the log message line
   *             as a String
   * @return the timestamp associated with the log message, throws an exception if
   *         no timestamp is found
   */
  def parseLine(line: String): LocalTime = {
    timeRegex.findFirstIn(line) match {

      case Some(value) => getTimestamp(value)
      case None => logger.error("String in log file does not contain a timestamp")
        throw new IllegalStateException("Log file does not contain correct log lines")
    }


  }

  /**
   * this function does a binary search of a timestamp "timestamp" in the array "arr"
   * ASSUMED SORTED FOR TIMESTAMP that is supposed to be an array of log lines.
   * In particular, it does the following :
   *    1) verifies the trivial cases in which the timestamp cannot be part of the array,
   *        so when the first entry of the array is greater or the last entry is less
   *    2) verifies the trivial cases in which the timestamp belongs to the array,
   *        so when it is equal to the first or the last entry of the array
   *    3) executes a recursive binary search, returning the right closest timestamp
   *       when extreme = 0, and the left closest interval when extreme = 1
   * for example, if
   * arr = Array(
   *  "00:00:00.003 [main] WARN  - ",
   *  "00:00:00.005 [main] WARN  - ",
   *  "00:00:00.008 [main] WARN  - ",
   *  "00:00:00.015 [main] WARN  - "
   *  )
   * timestamp = 00:00:00.004
   * it returns 1 (index of 00:00:00.005) if extreme = 0,
   * returns 1 (index of 00:00:00.003) if extreme = 1
   *
   * @param arr this parameter represents the search array in which the log lines are contained
   * @param timestamp this parameter represents the element to search in the array
   * @param extreme this parameter represents the extreme index element that we want to return
   *                , that is the left extreme if extreme = 0, the right if extreme = 1
   * @return the element if found, the closest element found according with extreme if
   *         exists, -1 otherwise
   */
  def findTimestamp(arr: Array[String], timestamp: LocalTime, extreme: Int): Int ={

    val head = parseLine(arr.head)
    val tail = parseLine(arr.last)

    logger.info("Start binary search")

    // check corner cases first
    // if the timestamp does not fit in the array
    if ((head > timestamp && extreme == 1)||
    (tail < timestamp && extreme == 0))  {
      logger.warn(s"Timestamp ${timestamp.toString} not found in the input array")
      return -1
    }

    // if the timestamp is equal or closest to one extreme

    if((head == timestamp) || (head > timestamp && extreme == 0)){
      logger.debug(s"Result found and equal to first array element")
      return 0
    }

    if( (tail == timestamp) || (tail < timestamp && extreme == 1)){
      logger.debug(s"Result found and equal to last array element")
      return arr.length - 1
    }

    val low = 0
    val high = arr.length - 1

    // binary search on the array
    recursiveBinarySearch(arr, timestamp, low, high, extreme)

  }

  /**
   * This recursive function executes a binary search on the "arr" ASSUMED SORTED FOR TIMESTAMP
   * array to search for the element "timestamp".
   *
   * @param arr this parameter represents the search array in which the log lines are contained
   * @param timestamp this parameter represents the element to search in the array
   * @param low this parameter represents the low index used to index the array in the binary search
   * @param high this parameter represents the high index used to index the array in the binary search
   * @param extreme this parameter represents the extreme index element that we want to return
   *                , that is the left extreme if extreme = 0, the right if extreme = 1
   * @return the element if found, the closest element found according with extreme if
   *         exists, -1 otherwise
   */
  @tailrec
  private def recursiveBinarySearch(arr: Array[String],
                                    timestamp: LocalTime,
                                    low: Int,
                                    high: Int,
                                    extreme : Int): Int = {


    // search is finished
    if (low == high + 1) {
      logger.debug(s"Result found for timestamp ${toString(timestamp)} at line ${low - extreme}, line is" +
        s" ${arr(low - extreme)}")
      return low - extreme
    }


    // Getting the middle element
    val middle = low + (high - low) / 2

    // get the timestamp from the log line
    val middleElem = parseLine(arr(middle))

    // If element is exactly equal to timestamp
    if (middleElem == timestamp) {
      logger.debug(s"Exact result found for timestamp ${toString(timestamp)} at line $middle, line is ${arr(middle)}")
      middle
    }
    // Searches in the left half
    else if (middleElem > timestamp) {
      logger.debug(s"Search in subArray[$low, ${middle - 1}]")
      recursiveBinarySearch(arr, timestamp, low, middle - 1, extreme)
    }
    // Searches in the right half
    else {
      logger.debug(s"Search in subArray[${middle + 1}, $high]")
      recursiveBinarySearch(arr, timestamp, middle + 1, high, extreme)
    }

  }


  /**
   * this function filters an array of log messages for the regex pattern
   * @param pattern this parameter represents the pattern used to filter log messages
   * @param logMessages this parameter represents the array that will be filtered
   *                    for regex pattern
   * @return a new array that contains the set of lines matching the regex pattern
   */
  def filterForPattern(pattern : Regex, logMessages : Array[String]): Array[String] = {
    logger.info("Searching for regex pattern in log lines")
    logMessages.filterNot(pattern.findFirstIn(_).isEmpty)
  }
}
