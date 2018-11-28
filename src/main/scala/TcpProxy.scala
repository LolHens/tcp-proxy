import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source, Tcp}
import akka.util.ByteString

import scala.concurrent.duration._

object TcpProxy {
  def main(args: Array[String]): Unit = {
    val inPort = args(0).toInt
    val outHost = args(1)
    val outPort = args(2).toInt

    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    Tcp().bind("0.0.0.0", inPort).to(Sink.foreach { connection =>
      connection.handleWith(
        Flow[ByteString]
          .map(processInbound)
          //.via(throttler(10, 100.millis))
          .via(Tcp().outgoingConnection(outHost, outPort))
          .map(processOutbound)
      )
    }).run()
  }

  def processInbound(data: ByteString): ByteString = {
    val newData = replaceString(data, "Host: localhost:2222", "Host: localhost:8080")
    logData(newData, "Request")
  }

  def processOutbound(data: ByteString): ByteString = {
    logData(data, "Response")
  }

  private def logData(data: ByteString, subject: String): ByteString = {
    println(
      "_" * 100 + "\n" +
        subject + ":\n" +
        "\n" +
        data.utf8String
    )
    data
  }

  private def throttler(bytes: Int, interval: FiniteDuration): Flow[ByteString, ByteString, NotUsed] =
    Flow[ByteString]
      .flatMapConcat(byteString => Source.fromIterator(() => byteString.grouped(bytes)))
      .throttle(1, interval)

  private def replaceString(data: ByteString, string: String, replacement: String): ByteString = {
    val search = ByteString.fromString(string)
    val index = data.indexOfSlice(search)
    if (index >= 0)
      data.take(index) ++ ByteString.fromString(replacement) ++ data.drop(index + search.size)
    else
      data
  }
}
