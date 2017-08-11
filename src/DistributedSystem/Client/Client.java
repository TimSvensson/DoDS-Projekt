/*
 * Project: DoDS-Projekt 
 * Class:   Client
 *
 * Version info
 * Created: 16/06/17
 * Author: Tim Svensson <svensson_tim@hotmail.se>
 */

package DistributedSystem.Client;

import DistributedSystem.Address;
import DistributedSystem.Flags;
import DistributedSystem.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
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

		// TODO Make all clients create a backupServer

public Address getfServerAddress() {
	return fServerAddress;
}

	//<editor-fold desc="FieldVariables">
private Address fServerAddress;

private Socket fServerSocket;
private BufferedReader fReader;
private PrintWriter fWriter;

private LinkedBlockingQueue<String> fQueue = new LinkedBlockingQueue<>();

private ArrayList<Address> fBackupServers;
//</editor-fold>

//<editor-fold desc="Constructors">
public Client(String pHostName, int pPortNumber) {
		fServerAddress = new Address(pHostName, pPortNumber);
}
//</editor-fold>

//<editor-fold desc="GettersAndSetters">

//</editor-fold>

//<editor-fold desc="PublicMethods">

@Override
public void run() {
		Logger.log("Starts Running.");
		boolean loop = true;
		while (loop) {
				if (fServerSocket == null || fServerSocket.isClosed()) {
						connect();
				}
				listen();
				disconnect();

				if (fBackupServers == null || fBackupServers.isEmpty()) {
						loop = false;
				} else {
						fServerAddress = fBackupServers.get(0);
						fBackupServers.remove(0);
				}
		}
		Logger.log("Stops Running.");
}

public void setup() {
		Thread t = new Thread(this);
		t.setName("Client-" + t.getId());
		t.setDaemon(true);
		t.start();

		try {
				Thread.sleep(100);
		} catch (InterruptedException pE) {
				pE.printStackTrace();
		}
}

public void connect() {
		Logger.log("Connecting to " + fServerAddress.getAddress() + " " + fServerAddress.getPort());
		try {
				fServerSocket = new Socket(fServerAddress.getAddress(), fServerAddress.getPort());
				Logger.log("Opening streams");
				fReader = new BufferedReader(new InputStreamReader(fServerSocket.getInputStream()));
				fWriter = new PrintWriter(fServerSocket.getOutputStream());
				write(Flags.client);
		} catch (IOException pE) {
				pE.printStackTrace();
		}
		Logger.log("Client connected and streams are open");
}

public void disconnect() {
		fWriter.close();
		try {
				fReader.close();
				fServerSocket.close();
		} catch (IOException pE) {
				pE.printStackTrace();
		}
		Logger.log("Socket closed.");
}

public void write(String pMessage) {
		Logger.log("Sending \"" + pMessage + "\".");
		fWriter.println(pMessage);
		fWriter.flush();
}

public String read() {
		// TODO Make not blocking
		while (!hasMessage()) {}
		return fQueue.poll();
}

public boolean hasMessage() {
		return !fQueue.isEmpty();
}

public boolean isClosed() {
		return fServerSocket.isClosed();
}

@Override
public String toString() {
		// TODO Add more relevant information like:
		// Size of MessageQueue
		// List of server_backup Servers
		return fServerSocket.toString();
}
//</editor-fold>

//<editor-fold desc="PrivateMethods">
private void listen() {
		Logger.log("Starting listening.");
		int unresolvedPings = 0;
		boolean loop = true;
		while (loop) {
				try {
						fWriter.println(Flags.ping);
						fWriter.flush();
						unresolvedPings++;

						while (fReader.ready()) {
								String s = fReader.readLine();
								StringTokenizer st = new StringTokenizer(s);
								switch (st.nextToken()) {
										case Flags.server_terminating:
												Logger.log("\"" + Flags.server_terminating +
														   "\" received.");
												loop = false;
												disconnect();
												return;
										case Flags.new_backup_server:
												setBackupServers(s);
												break;
										case Flags.ping:
												write(Flags.ping_response);
												break;
										case Flags.ping_response:
												unresolvedPings--;
												break;
										case Flags.new_client:
												Logger.log("New client connected: " + s);
												break;
										default:
												fQueue.offer(s);
												break;
								}
						}

						Thread.sleep(100);
				} catch (IOException pE) {
						Logger.log("IOException!");
				} catch (InterruptedException pE) {
						Logger.log("Interrupted!");
				}

				if (unresolvedPings >= 5) {
						Logger.log("Five or more unresolved pings.");
						loop = false;
				}
		}
		Logger.log("Stopping listening.");
}

private boolean setBackupServers(String pList) {
		StringTokenizer st = new StringTokenizer(pList);

		if (!Flags.new_backup_server.equals(st.nextToken())) {
				return false;
		}

		fBackupServers = new ArrayList<>();
		while (st.hasMoreTokens()) {

				String host = st.nextToken();
				int port = Integer.parseInt(st.nextToken());

				fBackupServers.add(new Address(host, port));
		}

		return true;
}
//</editor-fold>
}
