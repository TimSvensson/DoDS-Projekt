import DistributedSystem.Client.Client;
import DistributedSystem.Server.Server;
import com.google.gson.Gson;

import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public Game() {}

    public void run() {
        // Initiate the graphical user interface
        initGUI();

        // Check if a game-server already exists, if not then create and setup one, else join one
        if (iAmHost()) createGame(host, port);
        else joinGame(host, port);

        // Update the graphical user interface with the current gamestate
        updateGUI();

        while(true) {
            String newGameState = readFromServer();
            if (isMyTurn()) {
                play(newGameState);
                if (gameState.wasDoubleDice()) {
                    System.out.println("Double dice! You get another turn");
                    gameState.setCurrentPlayer(gameState.getPreviousplayer());
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
            if (answer.toLowerCase().equals("yes")) return true;
            if (answer.toLowerCase().equals("no")) return false;
            System.out.println("Please answer yes or no, you vile product of generations of incest and goatfucking.");
        }

    }

    private void createGame(String host, int port) {
        String [] squares = {"Street 1", "Street 2", "Street 3", "Street 4", "Street 5", "Street 6", "Street 7", "Street 8","Street 9", "Street 10"};
        gameState = new Board(4, squares);

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

    private void updateGUI() {
    }

    private void initGUI() {

    }

    private void startClient(String host, int port) {
    }

    private void terminateClient() {
        // TODO client.terminate();
    }

    private void sendToServer(Board newGameState) {
        client.write((new Gson()).toJson(newGameState));
    }

    private String readFromServer() {
        return client.read();
    }

    private void play(String JSONgameState) {
        gameState = (new Gson()).fromJson(JSONgameState, Board.class);
        gameState.movePlayer();
    }

    public boolean isMyTurn() {
        return false;
    }
}
