package server

import java.io.ObjectOutputStream
import java.net.Socket

object PlayerServerThread {

  /***
    * implicitly converts array of bytes to string
    * @param bytes array of bytes
    * @return String from received bytes
    */
  implicit def byteToString(bytes: Array[Byte]): String = {
    new String(bytes, "UTF-8")
  }

  /***
    * Contains and returns list of messages that will be written
    * to the output stream of the socket
    * @param sign
    * @return list of messages
    */
  def messages(sign: Char): List[String] = {
    val messages = List[String](
      "OK\n",
      "Cell is Busy, Try another\n",
      "Wrong input\n",
      "Not your Turn\n",
      s"Congratulations, Utils $sign Won!\n",
      "Tie occurred :(\n"
    )
    messages
  }
}

/***
  * This class handles all messages from input stream
  * and writes appropriate messages to ouput stream
  * @param player recieves input and output streams of players socket
  */
class PlayerServerThread(val player: Player) extends Runnable {
  import PlayerServerThread._

  override def run(): Unit = {

    while(!player.socket.isClosed) {
      val message: String = Stream.continually(player.socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
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
    val turn: String = Stream.continually(player.socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
    val position = turn.filter(x => x != ':').map(_.toInt - 48).toList
    position
  }

  /***
    * Validates input to the right format, tries to make turn
    * and calls respond method to the output message
    * @param turn
    */
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


  /***
    * Depending on received command creates prepares message
    * and sends it to the players socket
    * @param command number from list of messages
    */
  def respond(command: Int): Unit = {

    def sendGameField(vSocket: Socket) = {
      val objectOutputStream = new ObjectOutputStream(vSocket.getOutputStream)
      objectOutputStream.writeObject(player.gameSession.field)
    }

    if (command >= 4) {


      val message = s"END\n${messages(player.sign)(command)}"

      player.socket.getOutputStream.write(message.getBytes(), 0, message.length)
      player.opponentPlayer.socket.getOutputStream.write(message.getBytes(), 0, message.length)

      sendGameField(player.socket)
      sendGameField(player.opponentPlayer.socket)
    } else {

      player.socket.getOutputStream.write(messages(player.sign)(command).getBytes(), 0, messages(player.sign)(command).length)
      sendGameField(player.socket)
    }

    if (command == 0) {

      player.turn = false
      player.opponentPlayer.turn = true

      val messageForOpponent = s"OPPONENT\n${messages(player.sign)(command)}"

      player.opponentPlayer.socket.getOutputStream.write(messageForOpponent.getBytes(), 0, messageForOpponent.length)
      sendGameField(player.opponentPlayer.socket)
    }

  }

  /***
    * Reads command from inputStream, in case it receives START message
    * starts game
    */
  def readCommand(): Unit = {
    val message: String = Stream.continually(player.socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
    message.toUpperCase match {
      case "START" => MainServer.startGame()
    }
  }
}
