package server;


import java.util.Arrays;

public class GameSession {

    char[][] field;


    public GameSession() {

        field = new char[3][3];

        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = '_';
            }
        }
    }


    public void print() {
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.printf("%s ", field[i][j]);
            }
            System.out.println();
        }
    }

    public int update(int x, int y, char sign) {

        if (field[x][y] != '_') {
            return 1;
        }

        field[x][y] = sign;
        return 0;
    }
}
