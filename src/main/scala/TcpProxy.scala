import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Tcp}
import akka.util.ByteString

object TcpProxy {
  def main(args: Array[String]): Unit = {
    val inPort = args(0).toInt
    val outHost = args(1)
    val outPort = args(2).toInt

    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    def processInbound(data: ByteString): ByteString = data

    def processOutbound(data: ByteString): ByteString = data

    Tcp().bind("0.0.0.0", inPort).to(Sink.foreach { connection =>
      connection.handleWith(
        Flow[ByteString]
          .map(processInbound)
          .via(Tcp().outgoingConnection(outHost, outPort))
          .map(processOutbound)
      )
    }).run()
  }
}
