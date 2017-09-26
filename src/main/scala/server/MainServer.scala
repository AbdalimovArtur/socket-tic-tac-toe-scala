package server

import java.net.ServerSocket
import java.util

import scala.collection.mutable.ListBuffer

object MainServer extends App {

  val serverSocket = new ServerSocket(9090)
  val players = new util.ArrayList[Player]()


  while(!serverSocket.isClosed) {
    val connected = serverSocket.accept()
    val player = new Player(connected)
    players.add(player)
    new Thread(new PlayerServerThread(connected)).start()
  }
}
