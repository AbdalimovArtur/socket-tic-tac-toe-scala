package server;

import java.net.Socket;


public class Player {

    Socket socket;
    Player opponentPlayer;
    char sign;
    boolean turn;
    char[][] testField = new char[3][3];
    GameSession gameSession;

    public Player(Socket socket) {
        this.socket = socket;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }
}
