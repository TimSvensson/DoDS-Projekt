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

    public GameLoop() {

    }

    public Board readGamestate(PipedInputStream pipedInputStream) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(pipedInputStream));

        String jsonNewGamestate = in.readLine();
        Board newGamestate = gson.fromJson(jsonNewGamestate, Board.class);

        return newGamestate;
    }

    public void sendGamestate(Board game, PipedOutputStream pipedOutputStream) {

        PrintWriter out = new PrintWriter(new OutputStreamWriter(pipedOutputStream));

        String jsonGamestate = gson.toJson(game); // Skapar en jsonsträng av nuvarande gamestate
        out.println(jsonGamestate);
    }

    public void run() {
        //TODO Gör en while med scanner som bestämmer hur många spelare vi ska ha med i spelet.

        int port = Graphics.serverPort;
        String serverName = Graphics.serverName;

        if (Graphics.IS_SERVER) {

            Server server = new Server(port);
            Thread serverThread = new Thread(server);
            serverThread.start();

        }

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

            loop(pipeFromClient, pipeToClient);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void loop(Pipe pipeFromClient, Pipe pipeToClient) throws IOException {

        while(true) {

            Board newGamestate = readGamestate(pipeFromClient.getPipedInputStream());

                if (newGamestate.currentPlayer == 2) {
                    newGamestate.movePlayer(newGamestate.getListOfPlayers()[2]);
                    if (newGamestate.getDice().getDie1() != newGamestate.getDice().getDie2()) {
                        newGamestate.currentPlayer++;
                    }
                    sendGamestate(newGamestate, pipeToClient.getPipedOutputStream());
                }
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

