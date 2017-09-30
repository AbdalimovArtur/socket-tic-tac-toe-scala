package client

import java.net.Socket

object MainClient extends App {

  val socket = new Socket("localhost", 9090)

//  val currentPlayer = new Player();
  var started: Boolean = false

  new Thread(new PlayerClientThread(socket)).start()

  while(!socket.isClosed) {
    import PlayerClientThread.Regex

    val inMessage = io.StdIn.readLine()

    inMessage match {
      case r"\d:\d" =>
        if (started) {
          sendPosition(inMessage)
          println("Please wait for opponents turn")
        } else {
          println("Game is not started yet")
        }
      case "start" => sendCommand(inMessage)
      case _ =>
        println("Incorrect input format, try again")
    }
  }

  def sendPosition(position: String): Unit = {
    val message = s"TURN\n$position\n"
    socket.getOutputStream.write(message.getBytes, 0, message.length)
  }

  def sendCommand(command: String): Unit = {
    val message = s"COMMAND\n$command\n"
    socket.getOutputStream.write(message.getBytes(), 0, message.length)
  }


}

