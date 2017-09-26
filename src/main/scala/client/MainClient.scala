package client

import java.net.Socket

/**
  * Created by Artur on 26.09.2017.
  */
object MainClient extends App {

  val socket = new Socket("localhost", 9090)

  new Thread(new PlayerClientThread(socket)).start()

  while(!socket.isClosed) {
    val inMessage = io.StdIn.readLine()


    implicit class Regex(sc: StringContext) {
      def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
    }

    inMessage match {
      case r"\d:\d" => sendPosition(inMessage)
      case _ => print("Not OK")
    }
  }

  def sendPosition(position: String): Unit = {
    val message = s"TURN\n$position\n"
    socket.getOutputStream.write(message.getBytes, 0, message.length)
  }

}

