package HelperUtils

import com.typesafe.config.Config

import java.time.LocalTime
import java.time.format.{DateTimeFormatter, DateTimeParseException}
import scala.collection.immutable.ListMap
import scala.jdk.CollectionConverters.CollectionHasAsScala
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
   * this function returns a list of strings given a parameter name
   * in particular, it searches for the parameter name in the .config file, and if it does not find it, it
   * throws an exception
   *
   * @param stringListName this parameter represents the name of the configuration parameter that should be a list of strings
   * @throws IllegalArgumentException if the parameter name does not exists in the .config file of it is not a list
   * @return
   */
  private def getStringListSafe(stringListName: String): List[String] = {
    Try(config.getStringList(s"$configName.$stringListName").asScala.toList) match {
      case Success(value) => value
      case Failure(_) => logger.error(s"No config parameter $stringListName is provided")
        throw new IllegalArgumentException(s"No config data for $stringListName")
    }
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

  val protocol: String = getParam("protocol", "http")
  val ip: String = getParam("ip", "127.0.0.1")
  val port: String = getParam("port", "8080")
  val url: String = getParam("url", "timestampService")

  val awsIp: String = getParam("awsIp", "127.0.0.1")
  val awsPort: String = getParam("awsPort", "8080")
  val awsUrl: String = getParam("awsUrl", "timestampService")

  val awsBucket: String = getParam("awsBucket", "8080")
  val awsKey: String = getParam("awsKey", "timestampService")

}