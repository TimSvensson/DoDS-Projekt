import DistributedSystem.Address;
import DistributedSystem.Client.Client;
import DistributedSystem.Server.Server;
import com.google.gson.Gson;

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
        refreshListOfClients();

        while(true) {
            // Hosten ska inte läsa från servern första gången
            if (!isHost) gameState = readFromServer();

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

    private boolean iAmHost() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Are you the host?");

        while(true) {
            String answer = scanner.nextLine();
            if (answer.toLowerCase().equals("yes")) {
                isHost = true;
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
        gameState = new Board(4);

        server = new Server(port);
        client = new Client(host, port);

        server.setup();
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

        for (int i = 0; i < addresses.size() - 1; i++) {
            listOfClients.putIfAbsent(i, addresses.get(i));
        }
    }
}
