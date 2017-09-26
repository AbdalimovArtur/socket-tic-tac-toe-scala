package server

import java.net.Socket

class PlayerServerThread(val socket: Socket) extends Runnable {

  implicit def byteToString(bytes: Array[Byte]): String = {
    new String(bytes, "UTF-8")
  }

  override def run(): Unit = {
    while(!socket.isClosed) {

      val message: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
      message match {
        case "COMMAND" => readCommand()
        case "TURN" => readTurn()
      }
    }
  }


  def readTurn(): Unit = {
    val turn: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
    val position = turn.filter(x => x != ':').map(_.toInt - 48)
    println(position)
  }

  def readCommand(): Unit = {

  }
}
