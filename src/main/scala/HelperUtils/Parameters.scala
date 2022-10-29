package HelperUtils

import scala.util.{Failure, Success, Try}

/**
 * This module obtains configuration parameter values from application.conf and converts them
 * into appropriate scala types.
 *
 * The implementation follows the same structure of the Parameters class of the LogGeneration project
 *
 */
object Parameters {

  /**
   * This value is used to locate the configuration name at the root of the .config file
   */
  private val configName = "Homework2Config"

  /**
   * this value is used for logging purposes
   */
  private val logger = CreateLogger(classOf[Parameters.type])

  /**
   * this value represents the object used to access the .config file
   */
  private val config = ObtainConfigReference("Homework2Config") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }


  /**
   * this function returns the parameter value always represented as a string
   * (because the application uses only strings) corresponding to the name "pName"
   * in the .config file if exists, otherwise the default value "defaultVal" is chosen
   *
   * @param pName      the name of the .config parameter string to get
   * @param defaultVal the default value of the parameter if it does not exists in the .config file
   * @return the value of the parameter in the .config file associated with the name "pName" if exists, "defaultVal" otherwise
   */
  private def getParam(pName: String, defaultVal: String): String = {

    Try(config.getString(s"$configName.$pName")) match {
      case Success(value) => value
      case Failure(_) => logger.warn(s"No config parameter $pName is provided. Defaulting to $defaultVal")
        defaultVal
    }
  }



  /**
   * these values represent the public interface of the object Parameters
   * the description of each of them can be found in the .config file (should be located in the src/main/resources folder)
   * these values can't be changed once the jar is created
   */

  val pattern: String = getParam("Pattern", "([a-c][e-g][0-3]|[A-Z][5-9][f-w]){5,15}")
  val messageTypes: String = getParam("messageTypes", "(INFO|WARN|ERROR|DEBUG)")
  val timeRegexp: String = getParam("timeRegexp", "([0-9]{2}):([0-9]{2}):([0-9]{2}).([0-9]{3})")

  val timeout: Int = getParam("timeout", "10").toInt

  val awsFunctionUrl: String = getParam("awsFunctionUrl", "http://localhost:8080")

  val awsBucket: String = getParam("awsBucket", "peppe-bucket-test")
  val awsKey: String = getParam("awsKey", "LogFileGenerator.2022-10-17.0.log")

  val awsAccessKey: String = getParam("awsAccessKey", "")
  val awsSecretKey: String = getParam("awsSecretKey", "")

  val positiveResponseCode: Int = getParam("positiveResponseCode", "200").toInt
  val negativeResponseCode: Int = getParam("negativeResponseCode", "200").toInt

}