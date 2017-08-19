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
import java.util.StringTokenizer;

/**
 * Created by Haubir on 2017-08-09.
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

    private final String PREFIX = "%%";

    // Identifier flags
    private final String ALL_PLAYERS = PREFIX + "ALL_PLAYERS";

    // Message flags
    private final String GET_TOTAL_PLAYERS = PREFIX + "GET_TOTAL_PLAYERS";
    private final String ENOUGH_PLAYERS = PREFIX + "ENOUGH_PLAYERS";
    private final String CURRENT_AMOUNT_OF_PLAYERS = PREFIX  + "CURRENT_AMOUNT_OF_PLAYERS";
    private final String GAMESTATE = PREFIX + "GAMESTATE";

    /**
     * The Integer holds the turn of the Address to the corresponding in the game
     */
    HashMap<Integer, Address> listOfClients = new HashMap<>();

    List<Player> listOfPlayers = new ArrayList<>();

    public Game() {}

    public void run() {
        // Initiate the graphical user interface
        initGUI();

        // Check if a game-server already exists, if not then create and setup one, else join one
        if (iAmHost()) initGame();
        else joinGame(host, port); // TODO Make host and port dynamic

        System.out.println("isHost = " + isHost);

        if (isHost) setupGame();
        else waitForGameToStart();
        System.out.println("We finally have " + totalPlayers + " players!!");

        // Spel-loop
        while(true) {

            gameState = readFromServer();

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

            if (isGameOver()) break;
        }

        if (isHost) server.terminate();
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

    private void setupGame() {
        System.out.println("Waiting for the player count to rise to " + totalPlayers + "...");

        while (listOfPlayers.size() < totalPlayers) {

            refreshListOfClients();

            if (!client.hasMessage()) continue;
            String message = client.read();

            StringTokenizer tokenizer = new StringTokenizer(message);
            if (!tokenizer.hasMoreTokens()) continue;

            String recipient = tokenizer.nextToken();
            if (!(recipient.equals(ALL_PLAYERS) || recipient.equals(String.valueOf(client.getId())))) continue;

            String sender = tokenizer.nextToken();
            // TODO Extra: Lägg till check här för att se om avsändaren är med i address-listan (för säkerhet)

            String content = tokenizer.nextToken();
            switch (content) {
                case GET_TOTAL_PLAYERS:
                    client.write(createMessage(ALL_PLAYERS, CURRENT_AMOUNT_OF_PLAYERS + " " + String.valueOf(totalPlayers)));
                    break;
            }
        }

        client.write(createMessage(ALL_PLAYERS, ENOUGH_PLAYERS + " " + String.valueOf(totalPlayers)));
        System.out.println("We finally have " + totalPlayers + " players!!");

        // When all players have joined, the game is created
        gameState = new Board(listOfPlayers);

    }

    /**
     * Only for clients who join the game.
     */
    private void waitForGameToStart() {

        while (true) {
            client.write(createMessage(ALL_PLAYERS, GET_TOTAL_PLAYERS));

            String message = client.read();

            StringTokenizer tokenizer = new StringTokenizer(message);
            if (!tokenizer.hasMoreTokens()) continue;

            String recipient = tokenizer.nextToken();
            if (!(recipient.equals(ALL_PLAYERS) || recipient.equals(String.valueOf(client.getId())))) continue;

            String sender = tokenizer.nextToken();
            // TODO Extra: Lägg till check här för att se om avsändaren är med i address-listan (för säkerhet)

            String content = tokenizer.nextToken();
            String token = "";
            switch (content) {
                case CURRENT_AMOUNT_OF_PLAYERS:
                    token = tokenizer.nextToken();
                    totalPlayers = Integer.parseInt(token);
                    break;

                case ENOUGH_PLAYERS:
                    token = tokenizer.nextToken();
                    totalPlayers = Integer.parseInt(token);
                    return;
            }

        }

    }

    private boolean isGameOver() {
        return client.isClosed();
    }

    private void updateGUI(Board newGameState) {
        gameState = newGameState;

        // TODO!!! Fortsätt härifrån
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
        String gsToSend = gsonParser.toJson(gameState);
        createAndSendMessage(ALL_PLAYERS, GAMESTATE + " " + gsToSend);
    }

    private Board readFromServer() {
        String JSONgameState = "";

        while (true) {
            String message = client.read();

            StringTokenizer tokenizer = new StringTokenizer(message);
            if (!tokenizer.hasMoreTokens()) continue;

            String recipient = tokenizer.nextToken();
            if (!(recipient.equals(ALL_PLAYERS) || recipient.equals(String.valueOf(client.getId())))) continue;

            String sender = tokenizer.nextToken();
            // TODO Extra: Lägg till check här för att se om avsändaren är med i address-listan (för säkerhet)

            String content = tokenizer.nextToken();
            switch (content) {
                case GAMESTATE:
                    StringBuilder sb = new StringBuilder();
                    while (tokenizer.hasMoreTokens()) {
                        sb.append(tokenizer.nextToken());
                    }
                    JSONgameState = sb.toString();
                    break;
            }
            break;
        }

        return gsonParser.fromJson(JSONgameState, Board.class);
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
    
    private void createAndSendMessage(String recipient, String content) {
        client.write((createMessage(recipient, content)));
    }
    
    private String createMessage(String recipient, String content) {
        return String.join(" ", recipient, String.valueOf(client.getId()), content);
    }
}
