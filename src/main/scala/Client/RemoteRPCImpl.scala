package Client

import HelperUtils.{CreateLogger, Parameters}
import RemoteRPC.SearchGrpc.Search
import RemoteRPC.{TimestampRequest, TimestampResponse}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.language.postfixOps

/**
 * this class represents the remote procedure call implementation Search
 * In particular, it contains the method "IsTimeIntervalPresent" declared
 * in the protobuf interface description language.
 * The function sends an http request tp the lambda function
 */
class RemoteRPCImpl extends Search{


  /**
   * this attribute represents the http verb used to send the http request to the lambda endpoint
   */
  private val method = HttpMethods.GET

  /**
   * this attribute represents the url in which the lambda API RESTFul gateway is located
   */
  private val url = Parameters.awsFunctionUrl

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

  import system.dispatcher

  /**
   * this attribute represents the logger for this class
   */
  private val logger = CreateLogger(classOf[RemoteRPCImpl])

  /**
   * this method is used to create an http request object
   * in particular, it sets the method, url and body content, where the
   * body content is an array of bytes
   * In practice, the message is a marshalled protobuf message
   * @param message this parameter represents the marshalled sequence of
   *                bytes to send as object of the body
   * @return an http request object
   */
  private def getRequest(message: Array[Byte]): HttpRequest = {

    HttpRequest(
      method = method,
      uri = url,
      entity = HttpEntity(ContentTypes.`application/x-www-form-urlencoded`, message)
    )
  }

  /**
   * this function represents the remote procedure call.
   * In particular it does the following :
   *    1) creates and sends an http GET request to the lambda RESTFul API gateway with
   *       a body containing the marshalled protobuf message
   *    2) returns a future string containing a message based on the http response code
   * @param timestampRequest this parameter represents the protobuf object to send to the
   *                         lambda gateway
   * @return a future string containing a message based on the http response code
   */
  override def isTimeIntervalPresent(timestampRequest: RemoteRPC.TimestampRequest): scala.concurrent.Future[RemoteRPC.TimestampResponse]  = {

    logger.info(s"Sending request at url = $url")

    // get http request
    val request = getRequest(timestampRequest.toByteArray)

    // get future of an http response
    val responseFuture = Http().singleRequest(request)

    logger.info(s"Request sent")

    // return a future string based on the response code received
    responseFuture.map {
      case response@HttpResponse(StatusCodes.CustomStatusCode(200), _, _, _) =>
        logger.info(s"Positive response received")
        TimestampResponse(message = s"INFO MESSAGE : Positive response received with status code ${response.status}, time intervals are contained in the log file")


      case response@HttpResponse(StatusCodes.CustomStatusCode(480), _, _, _) =>
        logger.warn(s"Negative response received")
        TimestampResponse(message = s"WARNING MESSAGE : Negative response received with status code ${response.status}, time intervals are NOT contained in the log file")


      case response@HttpResponse(_, _, _, _) =>
        logger.error(s"Error response received")
        TimestampResponse(message = s"ERROR MESSAGE : Error in the request with status code ${response.status}")
    }
  }
}
