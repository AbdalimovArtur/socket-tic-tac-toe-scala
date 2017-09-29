package server

import java.io.{ByteArrayOutputStream, ObjectOutputStream}
import java.net.Socket

object PlayerServerThread {
  val messages = List[String](
    "Ok\n", "Cell is Busy, Try another\n", "Wrong input\n"
  )

  implicit def byteToString(bytes: Array[Byte]): String = {
    new String(bytes, "UTF-8")
  }
}

class PlayerServerThread(val socket: Socket, val player: Player) extends Runnable {
  import PlayerServerThread._

  val objectOutputStream = new ObjectOutputStream(socket.getOutputStream)

  /***
    * implicitly converts array of bytes to string
    * @param bytes
    * @return
    */


  override def run(): Unit = {

    while(!socket.isClosed) {
      val message: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
      message match {
        case "COMMAND" => readCommand()
        case "TURN" => makeMove(readTurn())
      }
    }
  }

  /***
    * Reads turn that player made, converting it to the vector of row and column
    * that will be used to update [server.GameSession].
    */
  def readTurn(): List[Int] = {
    val turn: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
    val position = turn.filter(x => x != ':').map(_.toInt - 48).toList
    position
  }

  def makeMove(turn: List[Int]) = {
    val formattedTurn = turn.filter(x => x >= 3 && x <=1 ).map(x => x - 1)

    formattedTurn.size match {
      case 2 =>
        respond(player.gameSession.update(formattedTurn.head, formattedTurn.tail.head, player.sign))
      case _ =>
        respond(2)
    }
  }

  def respond(command: Int): Unit = {
    import PlayerServerThread._
    socket.getOutputStream.write(messages(command).getBytes(), 0, messages(command).length)
    objectOutputStream.writeObject(player.gameSession.field)
  }

  def readCommand(): Unit = {
    val message: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
    message.toUpperCase match {
      case "Start" => MainServer.startGame()
    }
  }
}
