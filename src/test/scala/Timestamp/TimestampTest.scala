package Timestamp

import HelperUtils.Parameters
import Timestamp._
import com.mifmif.common.regex.Generex
import org.scalatest.PrivateMethodTester
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.util.matching.Regex

/**
 * this class tests the timestamp functions
 */
class TimestampTest extends AnyFlatSpec with Matchers with PrivateMethodTester{

  behavior of "Timestamp Test"

  private val regexp = Parameters.pattern
  private val randomStringGenerator = new Generex(regexp)

  it should "Throw an illegalArgumentException when trying to parse a bad format for the timestamp" in {

    an [IllegalArgumentException] should be thrownBy getTimestamp("")
    an [IllegalArgumentException] should be thrownBy getTimestamp("aaa")
    an [IllegalArgumentException] should be thrownBy getTimestamp("10:0:00.000")
    an [IllegalArgumentException] should be thrownBy getTimestamp("10:00:00:000")
    an [IllegalArgumentException] should be thrownBy getTimestamp("25:00:00.000")
    an [IllegalArgumentException] should be thrownBy getTimestamp("10:60:00.000")
    an [IllegalArgumentException] should be thrownBy getTimestamp("10:00:60.000")
    an [IllegalArgumentException] should be thrownBy getTimestamp("10:00:00.1000")
  }

  it should "Return the string of a timestamp in the correct format" in {

    val ts1 = getTimestamp("10:00:00.000")
    val ts2 = getTimestamp("00:00:00.000")

    Timestamp.toString(ts1) should be("10:00:00.000")
    Timestamp.toString(ts2) should be("00:00:00.000")

  }

  it should "Correctly parse a log message line into a timestamp" in {

    val line1 = "12:36:05.182 [main] WARN  - No config parameter Pattern is provided. Defaulting to ([a-c][e-g][0-3]|[A-Z][5-9][f-w]){5,15}"
    val line2 = "00:00:00.000 [main] WARN  - No config parameter Pattern is provided. Defaulting to ([a-c][e-g][0-3]|[A-Z][5-9][f-w]){5,15}"

    parseLine(line1) should be(getTimestamp("12:36:05.182"))
    parseLine(line2) should be(getTimestamp("00:00:00.000"))
  }

  it should "Perform a correct binary search when the timestamp to search belongs to the search array" in {

    val searchArray = Array(
      "00:00:00.000 [main] WARN  - ",
      "00:00:00.001 [main] WARN  - ",
      "00:00:00.002 [main] WARN  - ",
      "00:00:00.003 [main] WARN  - "
    )

    val ts = getTimestamp("00:00:00.001")

    findTimestamp(searchArray, ts, 0) should be(1)
    findTimestamp(searchArray, ts, 1) should be(1)

  }

  it should "Perform a correct binary search when the timestamp to search DOES NOT belongs to the search array" in {

    val searchArray = Array(
      "00:00:00.000 [main] WARN  - ",
      "00:00:00.005 [main] WARN  - ",
      "00:00:00.010 [main] WARN  - ",
      "00:00:00.015 [main] WARN  - "
    )

    val ts = getTimestamp("00:00:00.007")

    findTimestamp(searchArray, ts, 0) should be(2)
    findTimestamp(searchArray, ts, 1) should be(1)

  }

  it should "Fail the search when the timestamp to search for is lower than the first timestamp of the array" in {
    val searchArray = Array(
      "00:00:00.003 [main] WARN  - ",
      "00:00:00.005 [main] WARN  - ",
      "00:00:00.010 [main] WARN  - ",
      "00:00:00.015 [main] WARN  - "
    )

    val ts = getTimestamp("00:00:00.002")

    findTimestamp(searchArray, ts, 0) should be(0)
    findTimestamp(searchArray, ts, 1) should be(-1)
  }

  it should "Fail the search when the timestamp to search for is higher than the last timestamp of the array" in {
    val searchArray = Array(
      "00:00:00.003 [main] WARN  - ",
      "00:00:00.005 [main] WARN  - ",
      "00:00:00.010 [main] WARN  - ",
      "00:00:00.015 [main] WARN  - "
    )

    val ts = getTimestamp("00:00:00.016")

    findTimestamp(searchArray, ts, 0) should be(-1)
    findTimestamp(searchArray, ts, 1) should be(3)
  }

  it should "Simulate a real binary search over a set of log lines where both first and second timestamps belong to the search array" in {


    val startTimestamp = getTimestamp("00:00:00.003")
    val endTimestamp = getTimestamp("00:00:00.007")

    val searchArray = Array(
      "00:00:00.003 [main] WARN  - ",
      "00:00:00.005 [main] WARN  - ",
      "00:00:00.007 [main] WARN  - ",
      "00:00:00.015 [main] WARN  - "
    )

    findTimestamp(searchArray, startTimestamp, 0) should be(0)
    findTimestamp(searchArray, endTimestamp, 0) should be(2)
    findTimestamp(searchArray, startTimestamp, 1) should be(0)
    findTimestamp(searchArray, endTimestamp, 1) should be(2)

  }

  it should "Simulate a real binary search over a set of log lines where only the first timestamp belongs to the search array" in {


    val startTimestamp = getTimestamp("00:00:00.003")
    val endTimestamp = getTimestamp("00:00:00.007")

    val searchArray = Array(
      "00:00:00.003 [main] WARN  - ",
      "00:00:00.005 [main] WARN  - ",
      "00:00:00.008 [main] WARN  - ",
      "00:00:00.015 [main] WARN  - "
    )

    findTimestamp(searchArray, startTimestamp, 0) should be(0)
    findTimestamp(searchArray, endTimestamp, 1) should be(1)

  }

  it should "Simulate a real binary search over a set of log lines where only the second timestamp belongs to the search array" in {


    val startTimestamp = getTimestamp("00:00:00.003")
    val endTimestamp = getTimestamp("00:00:00.007")

    val searchArray = Array(
      "00:00:00.002 [main] WARN  - ",
      "00:00:00.005 [main] WARN  - ",
      "00:00:00.007 [main] WARN  - ",
      "00:00:00.015 [main] WARN  - "
    )

    findTimestamp(searchArray, startTimestamp, 0) should be(1)
    findTimestamp(searchArray, endTimestamp, 1) should be(2)

  }

  it should "Simulate a real binary search over a set of log lines where the first and the second timestamp DO NOT belong to the search array" in {


    val startTimestamp = getTimestamp("00:00:00.003")
    val endTimestamp = getTimestamp("00:00:00.007")

    val searchArray = Array(
      "00:00:00.002 [main] WARN  - ",
      "00:00:00.005 [main] WARN  - ",
      "00:00:00.008 [main] WARN  - ",
      "00:00:00.015 [main] WARN  - "
    )

    findTimestamp(searchArray, startTimestamp, 0) should be(1)
    findTimestamp(searchArray, endTimestamp, 1) should be(1)

  }

  it should "Correctly filter for the designated pattern" in {

    val logMessages = Array(
      "00:00:00.002 [main] WARN  - " + randomStringGenerator.random(),
      "00:00:00.005 [main] WARN  - " + randomStringGenerator.random(),
      "00:00:00.008 [main] WARN  - ",
      "00:00:00.015 [main] WARN  - "
    )

    val expected = logMessages.slice(0, 2)

    filterForPattern(new Regex(regexp), logMessages) should be(expected)

  }

  it should "Return an empty array if a set of log messages does not contain the regexp pattern" in {

    val logMessages = Array(
      "00:00:00.002 [main] WARN  - ",
      "00:00:00.005 [main] WARN  - ",
      "00:00:00.008 [main] WARN  - ",
      "00:00:00.015 [main] WARN  - "
    )

    val expected = Array()

    filterForPattern(new Regex(regexp), logMessages) should be(expected)

  }

  it should "Return an empty array if a set of log messages is empty" in {
    val logMessages: Array[String] = Array()
    val expected = Array()

    filterForPattern(new Regex(regexp), logMessages) should be(expected)
  }

}
