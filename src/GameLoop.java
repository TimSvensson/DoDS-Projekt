import Network.Client.Client;
import Network.Server.Server;
import com.google.gson.*;

import java.io.*;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by axelhellman on 2016-12-08.
 */



////String ip (Player); Kanske ska länka detta i monopolet istället så att id:et länkar till en spelares IP
public class GameLoop {
    Gson gson = new Gson();

    public Monopoly readGamestate(PipedInputStream pipedInputStream) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(pipedInputStream));

        String jsonNewGamestate = in.readLine();
        Monopoly newGamestate = gson.fromJson(jsonNewGamestate, Monopoly.class);

        return newGamestate;
    }

    public void sendGamestate(Monopoly game, PipedOutputStream pipedOutputStream) {

        PrintWriter out = new PrintWriter(new OutputStreamWriter(pipedOutputStream));

        String jsonGamestate = gson.toJson(game); // Skapar en jsonsträng av nuvarande gamestate
        out.println(jsonGamestate);
    }

    public void run() {
        //TODO Gör en while med scanner som bestämmer hur många spelare vi ska ha med i spelet.


        int port = 9000;
        String serverName = "localhost";

        Server server = new Server(port);
        Thread serverThread = new Thread(server);
        serverThread.start();

        Pipe pipeToClient;
        Pipe pipeFromClient;

        try {
            pipeToClient = new Pipe();
            pipeFromClient = new Pipe();

            Thread clientThread =
                    new Thread(
                            new Client(
                                    pipeToClient.getPipedInputStream(),
                                    pipeFromClient.getPipedOutputStream(),
                                    serverName,
                                    port));
            clientThread.start();


            int numberOfPlayers = 10; // Ändra den till vad du vill Adam
            String[] names = {"a", "b", "c", "d", "e"}; // Namn till rutorna
            Monopoly game = new Monopoly(numberOfPlayers, names); // Nytt spel skapas

            System.out.println("Game created");

            while(true) {
                Monopoly newGamestate = readGamestate(pipeFromClient.getPipedInputStream());

                if (newGamestate.currentTurn == 2) {
                    game.board.movePlayer(game.board.getListOfPlayers()[2], game.dice);
                    if (game.getDice().getDie1() != game.getDice().getDie2()) {
                        game.currentTurn++;
                    }
                    sendGamestate(game, pipeToClient.getPipedOutputStream());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class Pipe {

    private final PipedInputStream pipedInputStream;
    private final PipedOutputStream pipedOutputStream;

    Pipe() throws IOException {

        pipedInputStream = new PipedInputStream();
        pipedOutputStream = new PipedOutputStream();

        pipedInputStream.connect(pipedOutputStream);
    }

    public PipedInputStream getPipedInputStream() {
        return pipedInputStream;
    }

    public PipedOutputStream getPipedOutputStream() {
        return pipedOutputStream;
    }
}