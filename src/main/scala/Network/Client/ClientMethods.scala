package Network.Client

import HelperUtils.{CreateLogger, Parameters}
import RemoteRPC.{TimestampRequest, TimestampResponse}
import Network.Client.HTTPClient.logger
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethod, HttpRequest, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import akka.util.ByteString

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}

object ClientMethods {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  import system.dispatcher

  private val logger = CreateLogger(classOf[ClientMethods.type])

  private def getRequest(message: Array[Byte], uri : String, method : HttpMethod): HttpRequest = {

    HttpRequest(
      method = method,
      uri = uri,
      entity = HttpEntity(ContentTypes.`application/x-www-form-urlencoded`, message)
    )
  }

  def sendRequest(timestampRequest: TimestampRequest,
                  ip : String,
                  port : String,
                  url : String,
                  protocol : String,
                  method : HttpMethod): TimestampResponse = {

    val uri = s"$protocol://$ip:$port/$url"

    logger.info(s"request at uri = $uri")

    val request = getRequest(timestampRequest.toByteArray, uri, method)

    val responseFuture = Http().singleRequest(request)
    val timeout = 2 seconds

    logger.info(s"Request sent")

    val timestampResponse = Await.result(
      responseFuture
        .flatMap { resp => resp.entity.toStrict(timeout) }
        .map { strictEntity => TimestampResponse.parseFrom(strictEntity.data.utf8String.getBytes) },
      timeout
    )

    logger.info(s"Response received")

    timestampResponse

  }

}
