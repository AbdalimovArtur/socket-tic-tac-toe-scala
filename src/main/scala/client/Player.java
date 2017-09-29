package client;

/**
 * Created by Artur on 29.09.2017.
 */
public class Player {

    char[][] field;

    public Player() {
        field = new char[3][3];
    }

    public void updateField(Object ob) {
        field = (char[][])ob;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.printf("%s ", field[i][j]);
            }
            System.out.println();
        }
    }
}
