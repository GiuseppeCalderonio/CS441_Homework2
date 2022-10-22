package Network.Server

import RemoteRPC.{TimestampRequest, TimestampResponse}
import HelperUtils.{CreateLogger, Parameters}
import Network.Client.ClientMethods
import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.server.Directives.{as, complete, entity, path, post}
import akka.http.scaladsl.server.Route

import java.net.{URI, URL}

object ServerMethods {

  private val url = Parameters.url
  private val awsIp = Parameters.awsIp
  private val awsPort = Parameters.awsPort
  private val awsUrl = Parameters.awsUrl
  private val awsProtocol = Parameters.protocol

  private val logger = CreateLogger(classOf[ServerMethods.type])


  val route: Route = (path(url) & post) {

    logger.info(s"Received message")

    entity(as[String]) { message: String =>

      val timestampRequest = TimestampRequest.parseFrom(message.getBytes())

      logger.info(s"Message content serialized as json : $timestampRequest")

      val timestampResponse = callLambda(timestampRequest = timestampRequest)

      complete(timestampResponse.toByteArray)
    }

  }

  def callLambda(timestampRequest: TimestampRequest): TimestampResponse = {

    // Create HTTP url

      logger.info(s"Sending request to $awsUrl")
    // Send HTTP GET request to AWS Lambda endpoint, then read and process input

    val timestampResponse = ClientMethods.sendRequest(timestampRequest, awsIp, awsPort, awsUrl, awsProtocol, HttpMethods.GET)

    timestampResponse

  }

}
