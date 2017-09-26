package server;

import java.net.Socket;


public class Player {

    Socket socket;
    Socket opponentSocket;
    char sign;
    boolean turn;


    public Player(Socket socket) {
        this.socket = socket;
    }
}
