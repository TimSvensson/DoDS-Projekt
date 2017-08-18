import DistributedSystem.Address;
import DistributedSystem.Client.Client;
import DistributedSystem.Server.Server;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    /**
     * The list of players
     */
    List<Player> listOfPlayers = new ArrayList<>();

    public Game() {}

    public void run() {
        // Initiate the graphical user interface
        initGUI();

        // Check if a game-server already exists, if not then create and setup one, else join one
        if (iAmHost()) initGame();
        else joinGame(host, port); // TODO Make host and port dynamic

        System.out.println("isHost = " + isHost);
        if(!isHost) totalPlayers = listOfPlayers.size();

        System.out.println("Waiting for the player count to rise to " + totalPlayers + "...");
        while (listOfClients.size() < totalPlayers) refreshListOfClients();
        System.out.println("We finally have " + totalPlayers + " players!!");

        // Spel-loop
        while(true) {
            // TODO Hosten ska inte läsa från servern första gången
            if (isHost) {
                isHost = false; // TODO måste gå att göra på ett bättre sätt -Tim
                gameState = new Board(listOfPlayers);
            }
            else {
                gameState = readFromServer();
                // Update the graphical user interface with the current gamestate
                updateGUI(gameState);
            }

            if (isMyTurn()) {
                play(gameState);
                if (gameState.wasDoubleDice()) {
                    System.out.println("Double dice! You get another turn");
                    gameState.setCurrentPlayer(gameState.getPreviousPlayer().getTurn());
                }
                sendToServer(gameState);
            }

            if (isGameOver()) break;
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
                System.out.println("\"" + ans + "\" is not a number?!");
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
                System.out.println("Please answer yes or no.");
            }
        }

        return isHost;
    }

    private void initGame() {
        server = new Server(port);
        try {
            server.setup();
        } catch (IOException e) {
            e.printStackTrace();
        }

        joinGame(host, port);
    }

    private void joinGame(String host, int port) {
        client = new Client(host, port);
        client.setup();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Connected to " + host + " at port " + port);

        for (int i = 0; i < 10; i++) refreshListOfClients();

        System.out.println("listOfClients.size(): " + listOfClients.size());
        System.out.println("listOfPlayers.size(): " + listOfPlayers.size());
    }

    private boolean isGameOver() {
        return client.isClosed();
    }

    private void updateGUI(Board newGameState) {
        gameState = newGameState;

        Player previousPlayer = gameState.getPreviousPlayer();
        Square currentSquare = gameState.getCurrentSquare();
        Square previousSquare = gameState.getPreviousSquare();

        System.out.println("Player " + previousPlayer.getId() + " tossed " + gameState.getDice().getTotal() + " and moved from " + previousSquare.getName() + " to " + currentSquare.getName());

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
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Type 'Toss dice' to play your round");
        boolean played = false;

        while (!played) {
            String input = null;
            try {
                input = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (input.toLowerCase().equals("toss dice")) {
                played = true;
            }
            else {
                System.out.println("You had one job. Why did you join the game if you weren't going to play you fuckface?");
            }
        }

        gameState.movePlayer();
    }

    private boolean isMyTurn() {
        refreshListOfClients();
        return client.getId() == listOfClients.get(gameState.getCurrentTurn()).getID();
    }

    private void refreshListOfClients() {
        List<Address> addresses = client.getClients();

        System.out.println("addresses.size(): " + addresses.size());

        listOfClients = new HashMap<>();
        listOfPlayers = new ArrayList<>();

        if (addresses != null) {
            System.out.println("addresses != null!");
            for (int i = 0; i < addresses.size(); i++) {
                listOfClients.put(i, addresses.get(i));
                listOfPlayers.add(new Player(i, "Player " + i, addresses.get(i).getID()));
            }
        }
    }
    
    private String createMessage(String recipient, String sender, String content) {
        return String.join(" ", recipient, sender, content);
    }
}
