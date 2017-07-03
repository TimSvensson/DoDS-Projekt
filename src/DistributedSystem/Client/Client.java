/*
 * Project: DoDS-Projekt 
 * Class:   Client
 *
 * Version info
 * Created: 16/06/17
 * Author: Tim Svensson <svensson_tim@hotmail.se>
 */

package DistributedSystem.Client;

import DistributedSystem.Logger;
import sun.rmi.runtime.Log;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

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
public class Client implements Runnable {

    //<editor-fold desc="FieldVariables">
    private String fHostName;
    private int fPortNumber;

    private Socket fHost;

    private BufferedReader fReader;
    private PrintWriter fWriter;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public Client(String pHostName, int pPortNumber) {
        fHostName = pHostName;
        fPortNumber = pPortNumber;
    }

    //</editor-fold>

    //<editor-fold desc="GettersAndSetters">

    //</editor-fold>

    //<editor-fold desc="PublicMethods">

    @Override
    public void run() {
        connect();
        listen();
    }

    public void connect() {
        try {
            Logger.log("Connecting to " + fHostName + " " + fPortNumber);
            fHost = new Socket(fHostName, fPortNumber);

            Logger.log("Opening streams");
            fReader = new BufferedReader(new InputStreamReader(fHost.getInputStream()));
            fWriter = new PrintWriter(fHost.getOutputStream());

            Logger.log("Client connected and streams are open");
        } catch (UnknownHostException pE) {
            pE.printStackTrace();
        } catch (IOException pE) {
            pE.printStackTrace();
        }
    }

    public void reconnect(String pHostName, int pPortNumber) throws IOException {
        disconnect();
        fHostName = pHostName;
        fPortNumber = pPortNumber;
        connect();
    }

    public void disconnect() throws IOException {
        Logger.log("Disconnecting.");
        fReader.close();
        fWriter.close();
        fHost.close();
    }

    public void write(String pMessage) {
        Logger.log("Sending \"" + pMessage + "\".");
        fWriter.println(pMessage);
        fWriter.flush();
    }

    public String read() throws IOException {
        return fReader.readLine();
    }
    //</editor-fold>

    //<editor-fold desc="PrivateMethods">
    private void listen() {

    }
    //</editor-fold>

}
