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
    Gson gsonParser = new Gson();


    public Game() {}

    public void run() {
        // Initiate the graphical user interface
        initGUI();

        // Check if a game-server already exists, if not then create and setup one, else join one
        if (iAmHost()) createGame(host, port);
        else joinGame(host, port);


        while(true) {
            Board newGameState = readFromServer();

            // Update the graphical user interface with the current gamestate
            updateGUI(newGameState);

            if (isMyTurn()) {
                play(newGameState);
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
            if (answer.toLowerCase().equals("yes")) return true;
            if (answer.toLowerCase().equals("no")) return false;
            System.out.println("Please answer yes or no, you vile product of generations of incest and goatfucking.");
        }

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
        // TODO client.terminate();
    }

    private void sendToServer(Board newGameState) {
        client.write(gsonParser.toJson(newGameState));
    }

    private Board readFromServer() {
        return gsonParser.fromJson(client.read(), Board.class);
    }

    private void play(Board JSONgameState) {
        gameState.movePlayer();
    }

    public boolean isMyTurn() {
        return false;
    }
}
