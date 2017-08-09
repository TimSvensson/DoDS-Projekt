import DistributedSystem.Client.Client;
import DistributedSystem.Server.Server;
import com.google.gson.Gson;

import java.io.Console;
import java.io.IOException;

/**
 * Created by axelhellman on 2016-12-08.
 */

public class Game implements Runnable {
    Server server;
    Client client;
    Board gameState;
    String host = "";
    int port = 0;

    public Game() {}

    public void init() {

        try {
            initGUI();

            if (iAmHost()) createGame(host, port);
            else joinGame(host, port);

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

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {

    }

    private boolean iAmHost() {
        Console console = System.console();
        System.out.println("Are you the host?");

        String answer = console.readLine();

        while(true) {
            if (answer.toLowerCase().equals("Yes")) return true;
            if (answer.toLowerCase().equals("No")) return false;
            System.out.println("Please answer yes or no, you product of generations of incest and goatfucking.");
        }

    }

    private void createGame(String host, int port) {
        String [] playerNames = {"Player 1", "Player 2", "Player 3", "Player 4"};
        gameState = new Board(4, playerNames);

        (server = new Server(port)).setup();
        (client = new Client(host, port)).setup();
    }

    private void joinGame(String host, int port) {
        (client = new Client(host, port)).setup();
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
