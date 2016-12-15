package Network.Server;

/**
 * Created by timsvensson on 11/12/16.
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {

    private class ClientListObj {

        ServerClientConnection serverClientConnection;
        int port;

        public ClientListObj(ServerClientConnection c, int p) {
            this.serverClientConnection = c;
            this.port = p;
        }
    }

    private ArrayList<ClientListObj> cList;

    private final int serverPort;
    private ServerSocket serverSocket;
    private int clientPort;
    private final Database db;

    public Server(int port) {
        this.serverPort = port;
        this.clientPort = this.serverPort + 1;
        this.db = new Database();
    }

    public void Start() {

        System.out.println("Attempting to create server socket with port " + this.serverPort);
        while (true) {

            try {
                this.serverSocket = new ServerSocket(this.serverPort);
                System.out.println("serverClientConnection created");

                Listen();

            } catch (IOException e) {
                System.out.println("Exception in Start()");
                e.printStackTrace();
            }
        }
    }

    private void Listen() {

        System.out.println("Listening to port " + this.serverPort);
        try (Socket tmpClient = this.serverSocket.accept()) {

            System.out.println("Connecting...");

            Connect(tmpClient);

            tmpClient.close();
            this.clientPort++;
            System.out.println("Port " + tmpClient.getLocalPort() + " closed");

            this.serverSocket.close();
            System.out.println("ServerClientConnection closed");

        } catch (IOException e){
            System.out.println("Exception in Listen()");
            e.printStackTrace();
        }
    }

    private void Connect(Socket client) throws IOException {

        System.out.println("Connected to " + client.toString());

        PrintWriter out = new PrintWriter(client.getOutputStream(), true);

        System.out.println("Sent: " + this.clientPort);
        out.println(this.clientPort);

        new Thread(new ServerClientConnection(this.clientPort, this.db)).start();

    }
}

// TODO Add logger class for multi-threaded handling of log entries