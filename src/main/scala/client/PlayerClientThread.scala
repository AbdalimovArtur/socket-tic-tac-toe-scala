package client

import java.net.Socket

/**
  * Created by Artur on 26.09.2017.
  */
class PlayerClientThread(val socket: Socket) extends Runnable {

  override def run(): Unit = {

    while(!socket.isClosed) {
      Thread.sleep(1000)
    }
  }
}
