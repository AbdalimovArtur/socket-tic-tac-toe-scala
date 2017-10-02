package server

import java.net.{ServerSocket, Socket}
import java.util

/***
  * Starting point of Server application
  * Handles all connections to ServerSocket, and holds all players
  * as static instance of ArrayList, objects in scala works as Singleton objects
  *
  * @author Artur Abdalimov
  */
object MainServer extends App {

  val serverSocket = new ServerSocket(9090)
  val players = new util.ArrayList[Player]()

  while(!serverSocket.isClosed) {
    val connected = serverSocket.accept()
    val player = new Player(connected)
    players.add(player)
    new Thread(new PlayerServerThread(player)).start()
  }

  /***
    * This method initiates game, by creating common GameSession for
    * two players, sending to the sockets information about game
    */
  def startGame(): Unit = {

    val first = players.get(0)
    val second = players.get(1)

    first.opponentPlayer = second
    second.opponentPlayer = first

    val commonGameSession = new GameSession()

    first.gameSession = commonGameSession
    second.gameSession = commonGameSession

    first.sign = 'X'
    second.sign = 'O'

    first.turn = false
    second.turn = true

    sendSettings(first.socket, first.sign, first.opponentPlayer.socket)
    sendSettings(second.socket, second.sign, second.opponentPlayer.socket)

    println("Game is started")
  }

  /***
    * Creates well-formatted message that will be recognized by sockets
    * on the client side
    * @param socket destination of the message
    * @param opponent socket destination of the message
    * @param sign players sign '
    */
  def sendSettings(socket: Socket, sign: Char, opponent: Socket): Unit = {

    val message = s"SETTINGS\n$sign\n$opponent\n"
    socket.getOutputStream.write(message.getBytes, 0, message.length)
  }

}
