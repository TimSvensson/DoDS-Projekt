import DistributedSystem.Address;
import DistributedSystem.Client.Client;
import DistributedSystem.Server.Server;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by axelhellman on 2016-12-08.
 */

public class Game implements Runnable {
    Server server;
    Client client;
    Board gameState;
    String host = "localhost";
    int port = 9000;
    Gson gsonParser = new Gson();
    int totalPlayers = 0;

    boolean isHost = false;

    /**
     * The Integer holds the turn of the players in the game, the Address hold the IP and ID of the corresponding clients
     */
    HashMap<Integer, Address> listOfClients = new HashMap<>();

    public Game() {}

    public void run() {
        // Initiate the graphical user interface
        initGUI();

        // Check if a game-server already exists, if not then create and setup one, else join one
        if (iAmHost()) createGame(host, port);
        else joinGame(host, port);

        System.out.println("Waiting for the player count to rise to " + totalPlayers + "...");
        while (listOfClients.size() < totalPlayers) refreshListOfClients();
        System.out.println("We finally have " + totalPlayers + " players!! \n Game on bitches.");

        // Spel-loop
        while(true) {
            // TODO Hosten ska inte läsa från servern första gången
            if (isHost) isHost = false;
            else gameState = readFromServer();

            // Update the graphical user interface with the current gamestate
            updateGUI(gameState);

            if (isMyTurn()) {
                play(gameState);
                if (gameState.wasDoubleDice()) {
                    System.out.println("Double dice! You get another turn");
                    gameState.setCurrentPlayer(gameState.getPreviousPlayer().getTurn());
                }
                sendToServer(gameState);
            }

            if (gameIsOver()) break;
        }

        terminateClient();

    }

    private void setTotalPlayers(BufferedReader reader) {
        System.out.println("How many players should we expect today?");

        String ans = null;
        while (true) {
            try {
                ans = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                totalPlayers = Integer.parseInt(ans);
                System.out.println("Sar chaw bra, " + totalPlayers + " players it is!");
                break;
            }
            catch(NumberFormatException e) {
                System.out.println("Quzalqort qahbabab, are you so dumb to think that " + ans + " is a number?!\n Try again sagi sagbab ba let nayem");
            }
        }
    }

    private boolean iAmHost() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Are you the host?");

        String answer = null;
        while(true) {
            try {
                answer = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (answer.toLowerCase().equals("yes")) {
                isHost = true;
                setTotalPlayers(reader);
                break;
            }
            else if (answer.toLowerCase().equals("no")) {
                isHost = false;
                break;
            }
            else {
                System.out.println("Please answer yes or no, you vile product of generations of incest and goatfucking.");
            }
        }

        return isHost;
    }

    private void createGame(String host, int port) {
        gameState = new Board(totalPlayers);

        server = new Server(port);
        client = new Client(host, port);
    
        try {
            server.setup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        client.setup();

    }

    private void joinGame(String host, int port) {
        client = new Client(host, port);
        client.setup();
    }

    private boolean gameIsOver() {
        return client.isClosed();
    }

    private void updateGUI(Board newGameState) {
        gameState = newGameState;

        Player previousPlayer = gameState.getPreviousPlayer();
        Square previousSquare = gameState.getPreviousSquare();

        System.out.println("Player " + previousPlayer.getId() + " tossed " + gameState.getDice() + " and moved to " + previousSquare.getName());

    }

    private void initGUI() {
        System.out.println("Hello and welcome to the Monopoly game!");
    }

    private void terminateClient() {
        client.disconnect();
    }

    private void sendToServer(Board gameState) {
        client.write(gsonParser.toJson(gameState));
    }

    private Board readFromServer() {
        return gsonParser.fromJson(client.read(), Board.class);
    }

    private void play(Board JSONgameState) {
        gameState.movePlayer();
    }

    private boolean isMyTurn() {
        refreshListOfClients();
        return client.getId() == listOfClients.get(gameState.getCurrentTurn()).getID();
    }

    private void refreshListOfClients() {
        List<Address> addresses = client.getClients();

        if (addresses != null) {
            for (int i = 0; i < addresses.size() - 1; i++) {
                listOfClients.putIfAbsent(i, addresses.get(i));
            }
        }
    }
}
