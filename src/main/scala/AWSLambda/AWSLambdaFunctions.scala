package AWSLambda

import HelperUtils.{CreateLogger, Parameters}
import Timestamp.Timestamp.{filterForPattern, findTimestamp}
import Timestamp.TimestampPrimitives.Primitives

import java.time.LocalTime
import scala.util.matching.Regex

/**
 * This object represent the interface used by the lambda function to execute its function
 * In particular, it contains the methods "search" and "filter" to, respectively, search the
 * timestamps in the input array and filter the timestamps found for the regex pattern
 * The method search however calls the method filter, so that the lambda function executes just one call
 */
object AWSLambdaFunctions {

  /**
   * this attribute represents the regex pattern used to filter the log messages
   */
  private val pattern = new Regex(Parameters.pattern)

  /**
   * this attribute represents the logger of the object
   */
  private val logger= CreateLogger(classOf[AWSLambdaFunctions.type])

  /**
   * this method is used to search for timestamp intervals in a search array of log messages.
   * The method assumes that log messages are sorted by timestamp
   * In particular, the method :
   *    1) computes the first interval = timestamp and second interval = timestamp + deltaTimestamp
   *    2) does a binary search for timestamp on the input array lines for both the first interval
   *      and the second interval
   *    3) filters the log messages for regex pattern
   *
   * for example, if
   * pattern = "a"
   * lines = Array ("00:00:00.002 [main] WARN  - a",
   *                "00:00:00.005 [main] WARN  - a",
   *                "00:00:00.008 [main] WARN  - a",
   *                "00:00:00.015 [main] WARN  - a")
   * timestamp = 00:00:00.003
   * deltaTimestamp = 00:00:00.010
   *
   * then the method returns true since both "00:00:00.005" and "00:00:00.008" belong to
   * the time interval [00:00:00.003 ; 00:00:00.003 + 00:00:00.015] and both
   * match the regex pattern
   *
   *
   * @param timestamp this parameter represents the first timestamp to search
   *                  for the interval [first, second]
   * @param deltaTimestamp this parameter represents the time interval that, summed with
   *                       the timestamp, represents the second time interval
   *                       for the interval [first, second]
   * @param lines this parameter represents the search array of log messages
   * @return true if in the search array exist log messages that both belong to
   *         [timestamp, timestamp + deltaTimestamp] interval and match the regex pattern
   */
  def search(timestamp: LocalTime, deltaTimestamp: LocalTime, lines : Array[String]): Boolean = {

    logger.info(s"Search step starts")

    // compute time intervals
    val startTimestamp = timestamp
    val endTimestamp = timestamp + deltaTimestamp

    // binary search for the first time interval
    // extreme = 0 because we want the "right" neighbour of the time interval
    val firstIndex = findTimestamp(lines, startTimestamp, 0)

    // binary search for the second time interval
    // extreme = 1 because we want the "left" neighbour of the time interval
    val secondIndex = findTimestamp(lines, endTimestamp, 1)

    logger.info(s"Search step ended")

    // filter
    filter(firstIndex, secondIndex, lines)

  }

  /**
   * This function filters the input array of log messages "lines" for the regex pattern.
   * In particular, this function :
   *    1) checks whether the "firstIndex" and "secondIndex" parameters make sense
   *    2) Takes the subArray lines[firstIndex, secondIndex] in O(1) time
   *    3) Filters the subArray for the regex pattern
   *    4) Verifies if the new subArray filtered is empty
   *
   * for example, if
   * pattern = "a"
   * lines = Array ("00:00:00.002 [main] WARN  - a",
   *                "00:00:00.005 [main] WARN  - a",
   *                "00:00:00.008 [main] WARN  - a",
   *                "00:00:00.015 [main] WARN  - a")
   *
   * firstIndex = 1
   * secondIndex = 3
   * Then the function returns true because in the subArray ["00:00:00.005 [main] WARN  - a",
   *                                                         "00:00:00.008 [main] WARN  - a",
   *                                                         "00:00:00.015 [main] WARN  - a"]
   * exists at least one line that metches the regex pattern
   *
   * @param firstIndex this parameter represents the first index
   * @param secondIndex this parameter represents the second index
   * @param lines this parameter represents the input array to filter
   * @return false if the indexes are not consistent or does not exists a line
   *         in the subArray lines(firstIndex, secondIndex) that matches the regex
   *         pattern, true otherwise
   */
  def filter(firstIndex: Int, secondIndex : Int, lines : Array[String]) : Boolean = {

    logger.info(s"Filter step started")

    // first of all, verifies that the indexes are consistent
    if(firstIndex < 0 || secondIndex < 0 || secondIndex < firstIndex) return false

    // compute subArray
    val updatedLines = lines.slice(firstIndex, secondIndex + 1)

    // filter for regex
    val result = !filterForPattern(pattern, updatedLines).isEmpty

    logger.info(s"Filter step ended")

    result

  }

}
