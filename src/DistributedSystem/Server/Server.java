/*
 * Project: DoDS-Projekt 
 * Class:   Server
 *
 * Version info
 * Created: 16/06/17
 * Author: Tim Svensson <svensson_tim@hotmail.se>
 */

package DistributedSystem.Server;

import DistributedSystem.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class summary.
 * <p>
 * Class Description.
 * </p>
 *
 * @author Tim Svensson <svensson_tim@hotmail.se>
 * @version JDK 1.8
 * @since JDK 1.8
 */
public class Server implements Runnable {

    private class ClientSocket {
        private Socket fSocket;
        private PrintWriter fWriter;
        private BufferedReader fReader;

        public ClientSocket(Socket pSocket) throws IOException {
            fSocket = pSocket;
            fWriter = new PrintWriter(pSocket.getOutputStream());
            fReader = new BufferedReader(new InputStreamReader(pSocket.getInputStream()));
        }

        public void write(String s) {
            fWriter.println(s);
            fWriter.flush();
        }

        public String read() throws IOException {
            return fReader.readLine();
        }

        public void close() throws IOException {
            fWriter.close();
            fReader.close();
            fSocket.close();
        }
    }

    private class ServerDock implements Runnable {

        private ClientSocket fClient;

        public ServerDock(ClientSocket pClient) {
            fClient = pClient;
        }

        @Override
        public void run() {
            try {
                Logger.log("Waiting for incoming messages.");
                String s;
                while ((s = fClient.read()) != null) {
                    Logger.log("Received: " + s);
                    offerToIncomingQueue(s);
                }
            } catch (IOException pE) {
                Logger.log("IOException!");
                // pE.printStackTrace();
            }
        }
    }

    private class ServerEcho implements Runnable {

        @Override
        public void run() {

            Logger.log("ServerEcho is alive!");

            try {
                while (fIncomingQueue != null) {
                    String s = fIncomingQueue.take();
                    echo(s);
                }
            } catch (InterruptedException pE) {
                pE.printStackTrace();
                Logger.log("I was interrupted!");
            }
        }

        private void echo(String s) {
            Logger.log("Echoing \"" + s + "\"");
            for (ClientSocket client : fClients) {
                client.write(s);
            }
        }
    }

    //<editor-fold desc="FieldVariables">
    private boolean fTerminate = false;
    private int fPortNumber;
    private ArrayList<ClientSocket> fClients = new ArrayList<>();

    // ONLY TO BE MODIFIED THROUGH SYNCHRONIZED METHODS
    private LinkedBlockingQueue<String> fIncomingQueue = new LinkedBlockingQueue<>();
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public Server(int pPortNumber) {
        fPortNumber = pPortNumber;
    }
    //</editor-fold>

    //<editor-fold desc="GettersAndSetters">

    //</editor-fold>

    //<editor-fold desc="PublicMethods">
    @Override
    public void run() {
        Logger.log("Starting!");

        Thread seThread = new Thread(new ServerEcho(), "SE");
        seThread.setDaemon(true);
        seThread.start();

        try (ServerSocket lSS = new ServerSocket(fPortNumber)) {

            Logger.log("New ServerSocket created.");

            while (!fTerminate) {
                Logger.log("Waiting for new connection.");

                ClientSocket client = new ClientSocket(lSS.accept());
                fClients.add(client);
                ServerDock serverDock = new ServerDock(client);

                Thread t = new Thread(serverDock);
                t.setDaemon(true);
                t.start();

                Logger.log("New Client accepted.");
            }

        } catch (IOException pE) {
            pE.printStackTrace();
        }
        Logger.log("Stopping!");
    }

    public void terminate() {
        Logger.log("Terminating.");
        fTerminate = true;
    }
    //</editor-fold>

    //<editor-fold desc="PrivateMethods">
    private synchronized void offerToIncomingQueue(String s) {
        fIncomingQueue.offer(s);
    }
    //</editor-fold>
}