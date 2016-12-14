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

/*public Monopoly recreateMonopoly(JSONObject toRecreate) {

        }*/

    public static void main(String[] args) {
        //TODO Gör en while med scanner som bestämmer hur många spelare vi ska ha med i spelet.
        int numberOfPlayers = 10; // Ändra den till vad du vill Adam
        String[] names = {"a", "b", "c", "d", "e"}; // Namn till rutorna
        Monopoly game = new Monopoly(numberOfPlayers, names); // Nytt spel skapas

        System.out.println("Game created");


        Board b = game.getBoard(); // Tillgång till spelbrädet
        Dice d = game.getDice(); // Tillgång till speltärningarna

        Square currentSquare = b.movePlayer(b.getListOfPlayers()[0], d); // Kolla, vi flyttade till och med en spelare åt dig.
        System.out.println("Spelare har flyttats!");

        // TODO!!! Problem med att konvertera gamestate till JSON pga StackOverflowError. Misstänker att vi någonstans i klasshierarkin använder en cirkulär referens...
        // Relevant json-kod för att överföra gamestates nedan
        Gson gson = new Gson();
        String jsonPlayersList = gson.toJson(b.getListOfPlayers());
        String jsonSquareList = gson.toJson(b.getListOfSquares());
        System.out.println(jsonSquareList);

        String jsonGamestate = gson.toJson(game); // Skapar en jsonsträng av nuvarande gamestate
        System.out.println(jsonGamestate);
        // TODO!!! Kod för att skicka och ta emot jsonGamestate här

        // Följande kod tar emot och återskapar en gamestate
        Monopoly newGamestate = gson.fromJson(jsonGamestate, Monopoly.class);
        // Nu har det nya Monopoly-objektet och alla dess nästlade objekt återskapats hos mottagaren!


    }

}


