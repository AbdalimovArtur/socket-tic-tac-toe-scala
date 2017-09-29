package server

import java.net.{ServerSocket, Socket}
import java.util

import scala.collection.mutable.ListBuffer

object MainServer extends App {

  val serverSocket = new ServerSocket(9090)
  val players = new util.ArrayList[Player]()

  while(!serverSocket.isClosed) {
    val connected = serverSocket.accept()
    val player = new Player(connected)
    players.add(player)
    new Thread(new PlayerServerThread(connected, player)).start()
  }

  def startGame(): Unit = {

    val first = players.get(0)
    val second = players.get(1)

    first.opponentSocket = second.socket
    second.opponentSocket = first.socket

    val commonGameSession = new GameSession()

    first.gameSession = commonGameSession
    second.gameSession = commonGameSession

    first.sign = 'X'
    second.sign = 'O'

    sendSettings(first.socket, first.sign, first.opponentSocket)
    sendSettings(second.socket, second.sign, second.opponentSocket)

    println("Game is started")
  }

  def sendSettings(socket: Socket, sign: Char, opponent: Socket): Unit = {
    val message = s"SETTINGS\n$sign\n$opponent\n"
    socket.getOutputStream.write(message.getBytes, 0, message.length)
  }

}
