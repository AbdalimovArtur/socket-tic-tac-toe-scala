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
        return validate(sign);
    }

    public int validate(char sign) {


        boolean winHorizontally;
        boolean winVertically;
        boolean tie = true;

        for (int i = 0; i < 3; i++) {

            winHorizontally = true;
            winVertically = true;

            for (int j = 0; j < 3; j++) {
                if (field[i][j] != sign) winHorizontally = false;
                if (field[j][i] != sign) winVertically = false;
                if (field[j][i] == '_') tie = false;
            }

            if (winHorizontally || winVertically) {
                return 4;
            }
        }

        boolean winDiagonally = true;
        boolean winVDiagonally = true;

        for (int i = 0; i < 3; i++) {
            if (field[i][i] != sign) winDiagonally = false;
            if (field[i][2 - i] != sign) winVDiagonally = false;
        }

        if (winDiagonally || winVDiagonally) {
            return 4;
        }

        if (tie) {
            return 5;
        }

        return 0;
    }
}
