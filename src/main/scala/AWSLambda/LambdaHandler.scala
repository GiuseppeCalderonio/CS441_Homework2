package AWSLambda

import AWSLambda.AWSLambdaFunctions.search
import HelperUtils.{CreateLogger, Parameters}
import RemoteRPC._
import Timestamp.Timestamp._
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.lambda.runtime.events.{APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse}
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.s3.AmazonS3Client


/**
 * this object represents the lambda handler.
 * In particular, it extends RequestHandler interface with
 * APIGatewayV2HTTPEvent and APIGatewayV2HTTPResponse because it
 * implements the method handleRequest, which is the method called
 * by the lambda function when an APIGatewayV2HTTPEvent (http request) triggers the lambda
 * and returns an APIGatewayV2HTTPResponse (http response)
 */
object LambdaHandler extends RequestHandler[APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse] {

  /**
   * this attribute represents the aws s3 bucket in which reading the input log file
   */
  private val bucket = Parameters.awsBucket

  /**
   * this attribute represents the input log file in the bucket
   */
  private val key = Parameters.awsKey

  /**
   * this attribute represents the aws access key
   */
  private val awsAccessKey = Parameters.awsAccessKey

  /**
   * this attribute represents the aws secret key
   */
  private val awsSecretKey = Parameters.awsSecretKey

  /**
   * this attribute represents the set of credentials to access the aws s3 bucket
   */
  private val AWSCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey)

  /**
   * this attribute represents a logged user with the credentials AWSCredentials
   */
  private val awsClient = new AmazonS3Client(AWSCredentials)

  /**
   * this attribute represents the logger of the object
   */
  private val logger = CreateLogger(classOf[LambdaHandler.type])

  /**
   * this function is the "main" function of the aws lambda
   * In particular, when an http request is sent to the aws lambda RESTFul API Gateway,
   * this function is triggered:
   *    1) the body of the request is parsed as a PROTOBUF timestamp request
   *    2) the search function is invoked, that searches for timestamps in the interval
   *        that match the regex pattern
   *    3) returns a status code to the client (480 = fail, 200 = success)
   * @param apiGatewayEvent this parameter represents the http request
   * @param context this parameter represents the lambda context
   *                (contains information about the lambda and the request)
   * @return a positive http response if the search returns true, a negative http
   *         response if the search returns false
   */
  override def handleRequest(apiGatewayEvent: APIGatewayV2HTTPEvent, context: Context): APIGatewayV2HTTPResponse = {

    logger.info(s"Request received")

    // get teh body
    val body = apiGatewayEvent.getBody

    // parse as protobuf message
    val timestampRequest = TimestampRequest.parseFrom(body.getBytes())

    logger.info(s"Request parsed as a protobuf message")

    // get thetimestamps from the protobuf messages
    val timestamp = getTimestamp(timestampRequest.timestamp)
    val deltaTimestamp = getTimestamp(timestampRequest.deltaTimestamp)

    // get the input file content as an array of lines
    val lines = getFileAsArray

    // execute the search
    val result = search(timestamp, deltaTimestamp, lines)

    // get the status code
    val statusCode = getStatusCode(result)

    logger.info(s"Request processed and sent back to client with response code : $statusCode")

    // return the request
    APIGatewayV2HTTPResponse.builder()
      .withStatusCode(statusCode)
      .build()
  }

  /**
   * this method gets the http status code as an integer based on a boolean input
   * @param result tis parameter represents the boolean input
   * @return 200 if result = true, 480 if result = false
   */
  private def getStatusCode(result : Boolean) : Int = {
    if(result) return 200
    480
  }

  /**
   * this method gets the input log file as an array of strings representing log messages.
   * In particular, it :
   *    1) attempts to login with the credentials
   *    2) gets the content of the object if it exists
   * @return
   */
  private def getFileAsArray: Array[String] = {

    logger.info(s"Getting content of file $key in bucket $bucket")

    if (awsClient.doesObjectExist(bucket, key)) { // if object exists
      // get the object
      awsClient.getObjectAsString(bucket, key).split("\n")
    }
    else {
      // file doesn't exist
      logger.error(s"The file $key or bucket $bucket does not exists")
      throw new IllegalArgumentException(s"The file $key or bucket $bucket does not exists")
    }

  }

}
