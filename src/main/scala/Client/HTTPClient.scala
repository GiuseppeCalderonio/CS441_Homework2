package Client

import HelperUtils.{CreateLogger, Parameters}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps

/**
 * This object represents the http gRPC client, where the main method starts
 * In particular, it does an RPC call to the lambda function
 */
object HTTPClient  {

  /**
   * this implicit attribute represents the system properties,
   * used for handling network
   */
  implicit val system: ActorSystem = ActorSystem()

  /**
   * this implicit attribute represents the materializer,
   * used to handle streams of bytes to send over the network
   */
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  /**
   * this attribute represents the timeout after which the request is considered failed
   */
  private val timeout = FiniteDuration.apply(Parameters.timeout, TimeUnit.SECONDS)

  /**
   * this attribute represents the logger for the object
   */
  private val logger = CreateLogger(classOf[HTTPClient.type])

  /**
   * this method represents the main method for the client.
   * In particular, it does the following :
   *    1) parses the input values as timestamps
   *    2) executes the RPC call
   *    3) waits synchronously for the result and prints the resulting string
   * @param args arguments of the main, ideally args(0) = "hh:mm:ss.SSS" , args(1) = "hh:mm:ss.SSS"
   */
  def main(args: Array[String]): Unit ={

    // get inputs
    val timestamp = args(0)
    val deltaTimestamp = args(1)

    logger.info(s"Start gRPC client with parameters: timestamp = $timestamp, deltaTimestamp = $deltaTimestamp")


    // create protobuf object
    val timestampRequest = RemoteRPC.TimestampRequest(timestamp = timestamp, deltaTimestamp = deltaTimestamp)

    logger.info(s"Remote procedure call started")

    // call remote procedure
    val futureTimestampResponse = new RemoteRPCImpl().isTimeIntervalPresent(timestampRequest)

    // wait synchronously for the result
    val result = Await.result(futureTimestampResponse, timeout)

    logger.info(s"Remote procedure call ended")

    // print the output message
    logger.debug(result.message)

    logger.info(s"Shutdown")

  }
}


