import com.google.gson.*;

import java.util.Arrays;

/**
 * Created by axelhellman on 2016-12-08.
 */



////String ip (Player); Kanske ska länka detta i monopolet istället så att id:et länkar till en spelares IP
public class Monopoly {
    Dice dice = new Dice();
    Board board;
    // String [] list_of_playerID; // Länkar ihop spelarnas id:n med ip:addresserna
    //private transient Logger LOGGER = Logger.getLogger(this.getClass().getName());
    String [] list_of_ips;
    int currentTurn;

    public Monopoly(int totalPlayer, String[] names) {
        board = new Board(totalPlayer, names);
        list_of_ips = null;
        currentTurn = 0;
    }

    public Dice getDice() {
        return dice;
    }

    public Board getBoard() {
        return board;
    }

    public void setDice(Dice dice) {
        this.dice = dice;
    }

    public void setBoard(Board board) {
        this.board = board;
    }


    /*
    public Monopoly readGamestate() {
        String jsonNewGamestate = in.readLine();
        Monopoly newGamestate = gson.fromJson(jsonNewGamestate, Monopoly.class);

        return newGamestate;
    }

    public void sendGamestate() {
        Gson gson = new Gson();
        String jsonGamestate = gson.toJson(game); // Skapar en jsonsträng av nuvarande gamestate
        out.println(jsonGamestate);
    }
    */
    public static void main(String[] args) {

        //TODO Gör en while med scanner som bestämmer hur många spelare vi ska ha med i spelet.
        int numberOfPlayers = 10; // Ändra den till vad du vill Adam
        String[] names = {"a", "b", "c", "d", "e"}; // Namn till rutorna
        Monopoly game = new Monopoly(numberOfPlayers, names); // Nytt spel skapas

        System.out.println("Game created");


        Board b = game.getBoard(); // Tillgång till spelbrädet
        Dice d = game.getDice(); // Tillgång till speltärningarna

        System.out.println("game.currentTurn: " + game.currentTurn);
        Square currentSquare = b.movePlayer(b.getListOfPlayers()[0], d); // Kolla, vi flyttade till och med en spelare åt dig.
        System.out.println("Spelare har flyttats!");
        game.currentTurn++;
        // Problem med att konvertera gamestate till JSON pga StackOverflowError när Square implementerar grafik
        // Relevant json-kod för att överföra gamestates nedan

        /**
         * Varje spelare kommer ha en while-loop där man tar emot det nya game-statet i en JSON-sträng och återskapar allt.
         * När gamestatet återskapats kollar spelaren vems tur det är just nu. Är det dens tur så körs Board:s movePlayer().
         *
         * Utkast:
         *
         * // Spelare 2:s loop
         * while(true) {
         *    Monopoly newGamestate = readGamestate();
         *    if (newGamestate.currentTurn == 2) {
         *       game.board.movePlayer(game.board.getListOfPlayers()[2], game.dice);
         *       if (getDie1() != getDie2() {
         *          game.currentTurn++;
         *       }
         *       sendGamestate(game);
         *    }
         * }
         */

        System.out.println("game.currentTurn: " + game.currentTurn);
        Gson gson = new Gson();
        String jsonGamestate = gson.toJson(game); // Skapar en jsonsträng av nuvarande gamestate


        // Testade att köra en loop där Player 1 och 2 tog emot nuvarande gamestate, spelade och skickade nytt gamestate till nästa spelare.
        // Funkade som det skulle 14/12

        /*while (game.currentTurn == 1 || game.currentTurn == 2) {
            Monopoly newGamestate = gson.fromJson(jsonGamestate, Monopoly.class);
            System.out.println("newGamestate.currentTurn: " + newGamestate.currentTurn);

            if (game.currentTurn == 1) {
                game.board.movePlayer(game.board.getListOfPlayers()[1], game.dice);
            }

            else if (game.currentTurn == 2) {
                game.board.movePlayer(game.board.getListOfPlayers()[2], game.dice);
            }

            game.currentTurn++;
            jsonGamestate = gson.toJson(game); // Skapar en jsonsträng av nuvarande gamestate
            System.out.println(jsonGamestate);

            // Följande kod tar emot och återskapar en gamestate
            // Nu har det nya Monopoly-objektet och alla dess nästlade objekt återskapats hos mottagaren!

        }*/

        while(true) {
            Monopoly newGamestate = readGamestate();
            if (newGamestate.currentTurn == 2) {
                game.board.movePlayer(game.board.getListOfPlayers()[2], game.dice);
                if (getDie1() != getDie2() {
                    game.currentTurn++;
                }
                sendGamestate(game);
            }
        }

        Monopoly newGamestate = gson.fromJson(jsonGamestate, Monopoly.class);
        System.out.println("newGamestate.currentTurn: " + newGamestate.currentTurn);

    }

}


