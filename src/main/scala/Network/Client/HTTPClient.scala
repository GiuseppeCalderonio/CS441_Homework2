package Network.Client

import HelperUtils.{CreateLogger, Parameters}
import RemoteRPC.{TimestampRequest, TimestampResponse}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethod, HttpMethods, HttpRequest}
import akka.stream.ActorMaterializer

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps



object HTTPClient  {

  private val ip = Parameters.ip
  private val port = Parameters.port
  private val url = Parameters.url
  private val protocol = Parameters.protocol


  private val logger = CreateLogger(classOf[HTTPClient.type])

  def main(args: Array[String]): Unit ={

    val timestamp = args(0)
    val deltaTimestamp = args(1)

    logger.info(s"Start gRPC client with parameters: timestamp = $timestamp, deltaTimestamp = $deltaTimestamp")


    // create protobuf object
    val timestampRequest = TimestampRequest(timestamp = timestamp, deltaTimestamp = deltaTimestamp)

    val timestampResponse = ClientMethods.sendRequest(timestampRequest, ip, port, url, protocol, HttpMethods.POST)


    if(timestampResponse.completed){
      logger.info(s"Request completed, result is ${timestampResponse.timestamps}")
    }
    else {
      logger.warn(s"Response from server was negative")
    }






  }



}


