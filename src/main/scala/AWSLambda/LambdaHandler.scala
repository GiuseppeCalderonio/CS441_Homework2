package AWSLambda

import HelperUtils.{CreateLogger, Parameters}
import Network.Server.ServerMethods
import RemoteRPC.{TimestampRequest, TimestampResponse}
import Timestamp.Timestamp._
import Timestamp.TimestampPrimitives.Primitives
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.lambda.runtime.events.{APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client, AmazonS3ClientBuilder}
import org.slf4j.Logger

import java.time.LocalTime


object LambdaHandler extends RequestHandler[APIGatewayV2HTTPEvent, String] {


  private val logger= CreateLogger(classOf[LambdaHandler.type])

  private val bucket = Parameters.awsBucket
  private val key = Parameters.awsKey


  def handler(apiGatewayEvent: APIGatewayV2HTTPEvent, context: Context): APIGatewayV2HTTPResponse = {

    val body = apiGatewayEvent.getBody

    val timestampRequest = TimestampRequest.parseFrom(body.getBytes())

    logger.info(s"body = ${apiGatewayEvent.getBody}")



    val timestamp = getTimestamp(timestampRequest.timestamp)
    val deltaTimestamp = getTimestamp(timestampRequest.deltaTimestamp)

    search(timestamp, deltaTimestamp)

    APIGatewayV2HTTPResponse.builder()
      .withStatusCode(200)
      .withBody("okay")
      .build()
  }




  def search(timestamp: LocalTime, deltaTimestamp : LocalTime) : (Boolean, String) = {

    val lines = getFile.split("\n")

    val startTimestamp = timestamp
    val endTimestamp = timestamp + deltaTimestamp

    val firstIndex = findTimestamp(lines, startTimestamp, 1)

    val secondIndex = findTimestamp(lines, endTimestamp, 0)

    (firstIndex <= secondIndex, lines.slice(firstIndex, secondIndex).reduce( (s1, s2) => s1 + "" + s2))

  }


  def getFile: String = {

    val awsClient = AmazonS3ClientBuilder.defaultClient()
    awsClient.getObjectAsString(bucket, key)
  }

}
