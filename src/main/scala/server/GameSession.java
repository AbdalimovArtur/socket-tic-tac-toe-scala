package server;


/***
 * This class describes game state, instance of this class is common for
 * two players, to both of them has access to it.
 */
public class GameSession {

    char[][] field;

    /***
     * Constructor. Initiates game field with blank spaces in context of game.
     */
    public GameSession() {

        field = new char[3][3];

        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = '_';
            }
        }
    }

    /***
     * Updates game field after success turn of one of the player
     * @param x row
     * @param y column
     * @param sign which sign should be placed on (x, y)
     * @return success code or failure code
     */
    public int update(int x, int y, char sign) {

        if (field[x][y] != '_') {
            return 1;
        }

        field[x][y] = sign;
        return validate(sign);
    }

    /***
     * This method validates if one of the players won or
     * check isn't tie occurred
     * @param sign checks win conditions for particular player
     * @return code of tie, or win, or 0 if nothing happened
     */
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
