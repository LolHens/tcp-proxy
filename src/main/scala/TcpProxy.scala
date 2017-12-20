import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Tcp}

object TcpProxy {
  def main(args: Array[String]): Unit = {
    val inPort = args(0).toInt
    val outHost = args(1)
    val outPort = args(2).toInt

    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    Tcp().bind("0.0.0.0", inPort).to(Sink.foreach { connection =>
      connection.handleWith(Tcp().outgoingConnection(outHost, outPort))
    }).run()
  }
}
