package Timestamp

import org.scalatest.PrivateMethodTester
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import TimestampPrimitives.Primitives

/**
 * this test class tests the timestamp primitives
 */
class TimestampPrimitivesTest extends AnyFlatSpec with Matchers with PrivateMethodTester{

  behavior of "Timestamp Primitives Test"

  it should "Sum two simple timestamps without overflow" in {

    val ts1 = Timestamp.getTimestamp("10:03:04.564")
    val ts2 = Timestamp.getTimestamp("04:20:31.001")

    val sumTs = ts1 + ts2

    val result = Timestamp.getTimestamp("14:23:35.565")

    sumTs should be(result)
  }

  it should "Sum two timestamps with the rest" in {
    val ts1 = Timestamp.getTimestamp("10:03:04.564")
    val ts2 = Timestamp.getTimestamp("04:59:31.001")

    val sumTs = ts1 + ts2

    val result = Timestamp.getTimestamp("15:02:35.565")

    sumTs should be(result)
  }

  it should "Sum two timestamps and return to 00:00:00.000" in {
    val ts1 = Timestamp.getTimestamp("23:59:59.999")
    val ts2 = Timestamp.getTimestamp("00:00:00.001")

    val sumTs = ts1 + ts2

    val result = Timestamp.getTimestamp("00:00:00.000")

    sumTs should be(result)
  }

  it should "Correctly compare two simple timestamps" in {
    val ts1 = Timestamp.getTimestamp("10:03:04.564")
    val ts2 = Timestamp.getTimestamp("04:20:31.001")

    ts1 < ts2 should be (false)
    ts1 > ts2 should be (true)
    ts2 < ts1 should be (true)
    ts2 > ts1 should be (false)
  }

  it should "Compare two equal timestamps correctly" in {

    val ts1 = Timestamp.getTimestamp("10:03:04.564")
    val ts2 = Timestamp.getTimestamp("10:03:04.564")

    ts1 < ts2 should be(false)
    ts1 > ts2 should be(false)
    ts2 < ts1 should be(false)
    ts2 > ts1 should be(false)

  }

  it should "Verify if two timestamps are equal correctly" in {

    val ts1 = Timestamp.getTimestamp("10:03:04.564")
    val ts2 = Timestamp.getTimestamp("10:03:04.564")
    val ts3 = Timestamp.getTimestamp("04:20:31.001")

    ts1 == ts2 should be(true)
    ts2 == ts1 should be(true)
    ts1 == ts3 should be(false)
    ts2 == ts3 should be(false)
    ts3 == ts1 should be(false)
    ts3 == ts2 should be(false)
  }

}
