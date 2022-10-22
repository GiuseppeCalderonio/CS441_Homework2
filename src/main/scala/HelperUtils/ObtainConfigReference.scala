
package HelperUtils

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.{Failure, Success, Try}

/**
 * this object is used to get a reference that gives access to the .config file
 *
 * The implementation follows the same structure of the ObtainConfigReference class of the LogGeneration project
 */
object ObtainConfigReference {

  private val config = ConfigFactory.load()
  private val logger = CreateLogger(classOf[ObtainConfigReference.type])

  private def ValidateConfig(confEntry: String): Boolean = Try(config.getConfig(confEntry)) match {
    case Failure(exception) => logger.error(s"Failed to retrieve config entry $confEntry for reason $exception"); false
    case Success(_) => true
  }

  def apply(confEntry: String): Option[Config] = if (ValidateConfig(confEntry)) Some(config) else None
}