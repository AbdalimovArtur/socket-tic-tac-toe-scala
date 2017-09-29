package client

import java.net.Socket

/**
  * Created by Artur on 26.09.2017.
  */

object MainClient extends App {

  val socket = new Socket("localhost", 9090)

  val currentPlayer = new Player();

  new Thread(new PlayerClientThread(socket, currentPlayer)).start()

  while(!socket.isClosed) {
    import PlayerClientThread.Regex

    val inMessage = io.StdIn.readLine()

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

