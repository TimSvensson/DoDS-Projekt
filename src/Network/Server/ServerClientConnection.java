package Network.Server;

import java.io.*;
import java.net.*;

/**
 * Created by timsvensson on 09/12/16.
 */
public class ServerClientConnection implements Runnable {

    private class SocketReader implements Runnable {

        private final BufferedReader clientIn;
        private final int clientPort;
        private final Database db;

        private SocketReader(int port, BufferedReader cIn, Database db) {

            this.clientPort = port;
            this.clientIn = cIn;
            this.db = db;
        }

        @Override
        public void run() {
            ReadSocket();
        }

        private void ReadSocket() {

            System.out.println(this.clientPort + " Starting ReadSocket");
            try {
                while (Thread.currentThread().isInterrupted() == false) {
                    if (clientIn == null) {

                    }
                    if (clientIn.ready()) {
                        db.writeToLog(clientIn.readLine());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private Socket client;
    private final int port;
    private final Database db;

    private PrintWriter clientOut;
    private BufferedReader clientIn;
    private BufferedReader stdIn;

    public ServerClientConnection(int port, Database db) {
        this.port = port;
        this.db = db;
    }

    public void run() {

        System.out.println(this.port + " I AM ALIVE!");

        try {
            System.out.println(this.port + " Connecting...");
            Connect();

            System.out.println(this.port + " New thread; read socket");
            new Thread(new SocketReader(this.port ,this.clientIn, this.db)).start();

            System.out.println(this.port + " writing to socket");
            WriteToSocket();

            System.out.println(this.port + " closing serverClientConnection...");
            this.client.close();

        } catch (IOException e) {
            System.out.println(this.port + " Chat IOException");
            e.printStackTrace();
        }
    }

    private void Connect() throws IOException {

        java.net.ServerSocket serverSocket = new java.net.ServerSocket(this.port);
        System.out.println(this.port + " " + serverSocket.toString());

        this.client = serverSocket.accept();
        serverSocket.close();

        this.clientOut = new PrintWriter(this.client.getOutputStream(), true);
        this.clientIn = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        this.stdIn = new BufferedReader(new InputStreamReader(System.in));
    }

    private void WriteToSocket() {

        System.out.println(this.port + " WriteToSocket");

        int currentIndex = 0;
        while(true) {
            if ( currentIndex < this.db.Size() ) {
                this.clientOut.println(db.getLogAt(db.Size() - 1));
                currentIndex = db.Size() - 1;
            }
        }
    }
}