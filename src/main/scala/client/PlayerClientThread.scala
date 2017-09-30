package client

import java.io.ObjectInputStream
import java.net.Socket

object PlayerClientThread {

  implicit def byteToString(bytes: Array[Byte]): String = {
    new String(bytes, "UTF-8")
  }

  val messages = List[String](
    "OK", "Cell is Busy, Try another", "Wrong input", "Not your Turn"
  )

  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }
}

class PlayerClientThread(val socket: Socket) extends Runnable {

  import PlayerClientThread._

  override def run(): Unit = {

    while(!socket.isClosed) {
      val message: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
      message match {
        case mes if contains(message) => {
          println(mes)
          readArray()
        }
        case "OPPONENT" => readOpponentRespond()
        case "SETTINGS" => readSettings()
        case "TURN" => print(message)
        case _ => print(message)
      }
    }
  }

  def readArray(): Unit = {
    val objectInputStream = new ObjectInputStream(socket.getInputStream)
    Player.updateField(objectInputStream.readObject())
  }

  def readSettings(): Unit = {
    val sign: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
    val opponentInfo: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
    println(s"Your sign is $sign, and opponent $opponentInfo")
  }

  def contains(message: String): Boolean = !messages.find(x => x.equals(message)).isEmpty

  def readOpponentRespond(): Unit = {
    val respond: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
    respond match {
      case "OK" =>
        println("Opponent made turn")
        readArray()
        println("Now is your turn")
    }
  }
}
