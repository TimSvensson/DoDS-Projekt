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
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingDeque;
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
public class Client implements Runnable {

//<editor-fold desc="FieldVariables">
private String fHostName;
private int fPortNumber;

private Socket fSocket;

private BufferedReader fReader;
private PrintWriter fWriter;

private LinkedBlockingQueue<String> fQueue = new LinkedBlockingQueue<>();
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
		Logger.log("Starts Running.");
		if (fSocket == null || fSocket.isClosed()) {
				connect();
		}
		listen();
		Logger.log("Stops Running.");
}

public void setup() {
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
		
		try {
				Thread.sleep(100);
		} catch (InterruptedException pE) {
				pE.printStackTrace();
		}
}

public void connect() {
		Logger.log("Connecting to " + fHostName + " " + fPortNumber);
		try {
				fSocket = new Socket(fHostName, fPortNumber);
				Logger.log("Opening streams");
				fReader = new BufferedReader(new InputStreamReader(fSocket.getInputStream()));
				fWriter = new PrintWriter(fSocket.getOutputStream());
		} catch (IOException pE) {
				pE.printStackTrace();
		}
		
		Logger.log("Client connected and streams are open");
}

public void reconnect(String pHostName, int pPortNumber) throws IOException {
		disconnect();
		fHostName = pHostName;
		fPortNumber = pPortNumber;
		connect();
}

public void disconnect() throws IOException {
		fWriter.close();
		fReader.close();
		fSocket.close();
		Logger.log("Socket closed.");
}

public void write(String pMessage) {
		Logger.log("Sending \"" + pMessage + "\".");
		fWriter.println(pMessage);
		fWriter.flush();
}

public String read() {
		while (!hasMessage()) {}
		return fQueue.poll();
}

public boolean hasMessage() {
		return !fQueue.isEmpty();
}

public boolean isClosed() {
		return fSocket.isClosed();
}

@Override
public String toString() {
		// TODO Add more relevant information like:
		// Size of MessageQueue
		// List of backup Servers
		return fSocket.toString();
}
//</editor-fold>

//<editor-fold desc="PrivateMethods">
private void listen() {
		Logger.log("Starting listening.");
		String s = null;
		try {
				while ((s = fReader.readLine()) != null) {
						switch (s) {
								case "##server_terminating":
										Logger.log("\"##server_terminating\" received.");
										disconnect();
								default:
										fQueue.offer(s);
										break;
						}
				}
		} catch (IOException pE) {
				Logger.log("IOException!");
		}
		Logger.log("Stopping listening.");
}
//</editor-fold>

}
