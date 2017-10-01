package server;

import java.net.Socket;

/***
 * This class describes player, it holds address of player's and opponent's socket,
 * his/her sign, turn and instance of GameSession
 */
public class Player {

    Socket socket;
    Player opponentPlayer;
    char sign;
    boolean turn;
    GameSession gameSession;

    public Player(Socket socket) {
        this.socket = socket;
    }
}
