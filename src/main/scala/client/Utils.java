package client;


/***
 * Helper class that contains only one method
 */
public class Utils {

    /***
     * Receives object, converts it to the 2d array of characters,
     * displays to the players console
     * @param object
     */
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
