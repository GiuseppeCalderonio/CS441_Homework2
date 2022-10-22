package Network.Server

import HelperUtils._
import RemoteRPC.{TimestampRequest, TimestampResponse}
import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.google.gson.Gson

object HTTPServer {


  private val host = Parameters.ip
  private val port = Parameters.port

  private implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "AkkaHttpJson")

  private val logger = CreateLogger(classOf[HTTPServer.type])



  def main(args: Array[String]): Unit = {

    Http().newServerAt(host, port.toInt).bind(ServerMethods.route)

    logger.info(s"Server started at $host:$port")
  }

}
