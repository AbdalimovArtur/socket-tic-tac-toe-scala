package server

import java.io.ObjectOutputStream
import java.net.Socket

object PlayerServerThread {

  implicit def byteToString(bytes: Array[Byte]): String = {
    new String(bytes, "UTF-8")
  }

  def messages(sign: Char): List[String] = {
    val messages = List[String](
      "OK\n",
      "Cell is Busy, Try another\n",
      "Wrong input\n",
      "Not your Turn\n",
      s"Congratulations, Player $sign Won!\n",
      "Tie occurred :(\n"
    )
    messages
  }
}

class PlayerServerThread(val socket: Socket, val player: Player) extends Runnable {
  import PlayerServerThread._




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
    val formattedTurn = turn.filter(x => x <= 3 && x >= 1 ).map(x => x - 1)
    formattedTurn.size match {
      case 2 =>
        println("Updating cells")
        if (player.turn) respond(player.gameSession.update(formattedTurn.head, formattedTurn.tail.head, player.sign))
        else respond(3)
      case _ =>
        respond(2)
    }
  }



  def respond(command: Int): Unit = {

    def sendGameField(vSocket: Socket) = {
      val objectOutputStream = new ObjectOutputStream(vSocket.getOutputStream)
      objectOutputStream.writeObject(player.gameSession.field)
    }

    if (command >= 4) {


      val message = s"END\n${messages(player.sign)(command)}"

      player.socket.getOutputStream.write(message.getBytes(), 0, message.length)
      player.opponentPlayer.socket.getOutputStream.write(message.getBytes(), 0, message.length)

      sendGameField(socket)
      sendGameField(player.opponentPlayer.socket)
    } else {

      socket.getOutputStream.write(messages(player.sign)(command).getBytes(), 0, messages(player.sign)(command).length)
      sendGameField(socket)
    }

    if (command == 0) {

      player.turn = false
      player.opponentPlayer.turn = true

      val messageForOpponent = s"OPPONENT\n${messages(player.sign)(command)}"

      player.opponentPlayer.socket.getOutputStream.write(messageForOpponent.getBytes(), 0, messageForOpponent.length)
      sendGameField(player.opponentPlayer.socket)
    }

  }

  def readCommand(): Unit = {
    val message: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
    message.toUpperCase match {
      case "START" => MainServer.startGame()
    }
  }
}
