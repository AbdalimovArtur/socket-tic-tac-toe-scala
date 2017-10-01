package client

import java.net.Socket

/***
  * Starting point of client application
  * Creates PlayerClientThread that handles messages
  * from inputStream, and waits for input from player
  */
object MainClient extends App {

  val socket = new Socket("localhost", 9090)

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

  /***
    * Sends to the outputStream position where player wants to make turn
    * @param position
    */
  def sendPosition(position: String): Unit = {
    val message = s"TURN\n$position\n"
    socket.getOutputStream.write(message.getBytes, 0, message.length)
  }

  /***
    * Sends command to the outputStream that was written by player from keyboard
    * @param command
    */
  def sendCommand(command: String): Unit = {
    val message = s"COMMAND\n$command\n"
    socket.getOutputStream.write(message.getBytes(), 0, message.length)
  }


}

