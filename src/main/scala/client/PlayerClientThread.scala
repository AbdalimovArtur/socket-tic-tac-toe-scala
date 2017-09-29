package client

import java.io.ObjectInputStream
import java.net.Socket

/**
  * Created by Artur on 26.09.2017.
  */

object PlayerClientThread {

  implicit def byteToString(bytes: Array[Byte]): String = {
    new String(bytes, "UTF-8")
  }

  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }
}

class PlayerClientThread(val socket: Socket, val player: Player) extends Runnable {

  val objectInputStream = new ObjectInputStream(socket.getInputStream)

  override def run(): Unit = {
    import PlayerClientThread._

    while(!socket.isClosed) {
      val message: String = Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray
      message match {
        case "Ok" => readArray()
      }
    }
  }

  def readArray(): Unit = {
    player.updateField(objectInputStream.readObject())
  }
}
