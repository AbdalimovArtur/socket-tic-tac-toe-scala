package client

import java.io.ObjectInputStream
import java.net.Socket

object PlayerClientThread {

  /***
    * Implicitly converts array of bytes to the String
    * @param bytes array of bytes
    * @return String converted from bytes
    */
  implicit def byteToString(bytes: Array[Byte]): String = {
    new String(bytes, "UTF-8")
  }

  /***
    * List of messages that will be received from inputStream
    */
  val messages = List[String](
    "OK", "Cell is Busy, Try another", "Wrong input", "Not your Turn"
  )

  /***
    * implicit class that will be used when validating input
    * with positions
    * @param sc String which will be validated
    */
  implicit class Regex(sc: StringContext) {
    /***
      * Receives string, and using Regular expression, checks
      * it matching to the defined pattern
      * @return received string if it matches, otherwise None
      */
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }
}

/***
  * This class responsible for all messages read from input stream
  * @param socket
  */
class PlayerClientThread(val socket: Socket) extends Runnable {

  import PlayerClientThread._

  /***
    * Default method of that Implemented by every class that implements
    * Runable
    */
  override def run(): Unit = {

    while(!socket.isClosed) {
      val message: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
      message match {

        case mes if contains(message) =>
          println(mes)
          readArray()

        case "OPPONENT" => readOpponentRespond()
        case "SETTINGS" => readSettings()
        case "TURN" => print(message)
        case "END" => happyEnd()
        case _ => print(message)
      }
    }
  }

  /***
    * Reads object, that will be converted to the 2d Array in future
    */
  def readArray(): Unit = {
    val objectInputStream = new ObjectInputStream(socket.getInputStream)
    Utils.updateField(objectInputStream.readObject())
  }

  /***
    * Reads messages, that contains information about players sign and its opponent
    */
  def readSettings(): Unit = {
    val sign: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
    val opponentInfo: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
    println(s"Your sign is $sign, and opponent $opponentInfo")
    MainClient.started = true
  }

  /***
    * Checks is received message exists in list of all possible
    * message that common to server and client
    * @param message to check
    * @return true if list contains this message, false otherwise
    */
  def contains(message: String): Boolean = !messages.find(x => x.equals(message)).isEmpty

  /***
    * Depending on actions of opponent receives message and validates it
    * If opponent made successful turn, prints updated game field
    */
  def readOpponentRespond(): Unit = {
    val respond: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
    respond match {
      case "OK" =>
        println("Opponent made turn")
        readArray()
        println("Now is your turn")
    }
  }

  /***
    * This method prints information game end message, and closes connection of client
    */
  def happyEnd() = {
    val respond: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
    println(respond)
    readArray()
    MainClient.socket.close()
  }
}
