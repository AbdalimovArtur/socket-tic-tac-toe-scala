package client;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class Player {

    public static void updateField(Object object) {

        char[][] field = (char[][])object;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.printf("%s ", field[i][j]);
            }
            System.out.println();
        }
    }
}
