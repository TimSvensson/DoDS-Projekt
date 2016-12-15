import Network.Client.Client;
import Network.Server.Server;
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Created by axelhellman on 2016-12-08.
 */



////String ip (Player); Kanske ska länka detta i monopolet istället så att id:et länkar till en spelares IP
public class GameLoop {
    Gson gson = new Gson();

    public Monopoly readGamestate(BufferedReader in) throws IOException {
        String jsonNewGamestate = in.readLine();
        Monopoly newGamestate = gson.fromJson(jsonNewGamestate, Monopoly.class);

        return newGamestate;
    }

    public void sendGamestate(Monopoly game, PrintWriter out) {
        String jsonGamestate = gson.toJson(game); // Skapar en jsonsträng av nuvarande gamestate
        out.println(jsonGamestate);
    }

    public void run() {
        //TODO Gör en while med scanner som bestämmer hur många spelare vi ska ha med i spelet.


        int port = 9000;
        String serverName = "localhost";

        /*PrintWriter serverOut = new PrintWriter()
        BufferedReader serverIn = new BufferedReader()*/
        Server server = new Server(port);
        Thread serverThread = new Thread(server);
        serverThread.start();

        /*PrintWriter clientOut = new PrintWriter()
        BufferedReader clientIn = new BufferedReader()*/
        Client client = new Client();
        Thread clientThread = new Thread(server);
        clientThread.start();



        int numberOfPlayers = 10; // Ändra den till vad du vill Adam
        String[] names = {"a", "b", "c", "d", "e"}; // Namn till rutorna
        Monopoly game = new Monopoly(numberOfPlayers, names); // Nytt spel skapas

        System.out.println("Game created");

        while(true) {
            Monopoly newGamestate = readGamestate();
            if (newGamestate.currentTurn == 2) {
                game.board.movePlayer(game.board.getListOfPlayers()[2], game.dice);
                if (game.getDice().getDie1() != game.getDice().getDie2()) {
                    game.currentTurn++;
                }
                sendGamestate(game);
            }
        }
    }
}


