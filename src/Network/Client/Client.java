package Network.Client;

import java.io.*;
import java.net.*;

/**
 * Created by Tim on 2016-12-12.
 */
public class Client implements Runnable {

    private final BufferedReader userInput;
    private final PrintWriter userOutput;
    private final String serverName;
    private final int serverPort;

    public Client() {

        this.serverName = "localhost";
        this.serverPort = 9000;

        this.userInput = new BufferedReader(new InputStreamReader(System.in));
        this.userOutput = new PrintWriter(System.out);
    }

    public Client(PipedInputStream userInput, PipedOutputStream userOutput, String serverName,
                  int serverPort) {

        this.serverName = serverName;
        this.serverPort = serverPort;

        this.userInput = new BufferedReader(new InputStreamReader(userInput));
        this.userOutput = new PrintWriter(new OutputStreamWriter(userOutput));
    }

    @Override
    public void run() {
        try {


            System.out.println("Starting...");
            Socket socket = new Socket(serverName, serverPort);
            System.out.println("Socket connected");

            BufferedReader fromServer = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String s = fromServer.readLine();
            System.out.println("Received: " + s);


            Connect(serverName, Integer.parseInt(s));


        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + serverName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + serverName);
            System.exit(1);
        }
    }

    void Connect(String host, int port) {

        try (Socket sct = new Socket(host, port)) {

            Thread readerThread = new Thread(
                    new SocketReader(sct, this.userOutput, Thread.currentThread()));
            readerThread.setDaemon(true);
            readerThread.start();

            Write(sct);


        } catch (IOException e) {
            System.out.println("Exception in connect()");
            e.printStackTrace();
        }

        System.out.println("Connection terminated");
    }

    void Write(Socket sct) {

        try {
            PrintWriter pw = new PrintWriter(sct.getOutputStream());

            String s;
            while ((s = this.userInput.readLine()).equals("disconnect") == false) {

                if (sct.isClosed() || Thread.currentThread().isInterrupted()) {
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("write() interrupted...");
                    }
                    break;
                }

                pw.println(s);
                pw.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SocketReader implements Runnable {

        private final Socket sct;
        private final PrintWriter userOutput;
        private final Thread parentThread;

        private SocketReader(Socket sct, PrintWriter userOut, Thread parent) {
            this.sct = sct;
            this.userOutput = userOut;
            this.parentThread = parent;
        }

        private void Read() {

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(sct.getInputStream()));

                String s;
                while (((s = br.readLine()) != null)) {

                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("SocketReader interrupted...");
                        break;
                    }

                    this.userOutput.println(s);
                    this.userOutput.flush();
                }
            } catch (IOException e) {
                System.out.println("IOException in read()");
                this.parentThread.interrupt();
            }
        }

        @Override
        public void run() {
            Read();
            System.out.println("SocketReader terminating...");
        }
    }
}