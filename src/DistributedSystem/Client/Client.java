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

//<editor-fold desc="FieldVariables">
private boolean isRunning = true;
private boolean isDisconnected = true;

private String host;
private int port;
private int id;

int unresolvedPings = 0;
int unresolvedPingLimit = 6;
int sleepTime = 100;

private LinkedBlockingQueue<String> queueToUser = new LinkedBlockingQueue<>();
private LinkedBlockingQueue<String> queueToServer = new LinkedBlockingQueue<>();

private ArrayList<Address> backupServers;
private ArrayList<Address> clients;
//</editor-fold>

//<editor-fold desc="Constructors">
public Client(String host, int port) {
		this.host = host;
		this.port = port;
		
}
//</editor-fold>

//<editor-fold desc="GettersAndSetters">
public ArrayList<Address> getBackupServers() {
		return backupServers;
}

public ArrayList<Address> getClients() {
		return clients;
}

public int getId() {
		return id;
}

public String getHost() {
		return host;
}

public int getPort() {
		return port;
}
//</editor-fold>

//<editor-fold desc="PublicMethods">

@Override
public void run() {
		Logger.log("Starts Running.");
		clientLoop(host, port);
		Logger.log("Stops Running.");
}

/**
 * Creates a new thread for this class, makes it a daemon thread, and calls it's run() method.
 */
public void setup() {
		write(Flags.client);
		write(Flags.all_backup_servers);
		
		Thread t = new Thread(this);
		t.setName("Client-" + t.getId());
		t.setDaemon(true);
		t.start();
}

public void disconnect() {
		write(Flags.disconnect);
		isRunning = false;
}

public void write(String s) {
		queueToServer.offer(s);
}

public String read() {
		while (!hasMessage()) {
		}
		return queueToUser.poll();
}

public boolean hasMessage() {
		return !queueToUser.isEmpty();
}

public boolean isDisconnected() {
		return isDisconnected;
}

public boolean isClosed() {
		return !isRunning;
}

@Override
public String toString() {
		String s = "%s %i %i", host, port, id;
		return s;
}
//</editor-fold>

//<editor-fold desc="PrivateMethods">
private void clientLoop(String host, int port) {
		while (isRunning) {
				// Connect to the server.
				connect(host, port);
				isDisconnected = true;
				
				// If the termination was intended, break the loop.
				if (!isRunning) {
						break;
				}
				
				// Otherwise:
				// If there are no backup servers, isRunning client either way, or
				if (backupServers == null || backupServers.isEmpty()) {
						Logger.log("No backup servers. Terminating client.");
						isRunning = false;
				}
				// If there are backup servers, connect to one of them.
				else {
						host = backupServers.get(0).getAddress();
						port = backupServers.get(0).getPort();
						Logger.log("Using backup server: " + backupServers.get(0).toString());
						backupServers.remove(0);
						
						write(Flags.client);
						write(Flags.all_backup_servers);
				}
		}
}

private void connect(String host, int port) {
		Logger.log("Connecting to " + host + " " + port);
		
		try (Socket s = new Socket(host, port);
			 BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			 PrintWriter writer = new PrintWriter(s.getOutputStream())) {
				
				Logger.log("Client connected to " + host + " " + port + ".");
				isDisconnected = false;
				
				while (true) {
						try {
								if (!readProtocol(reader)) {
										break;
								}
								
								write(Flags.ping);
								unresolvedPings++;
								
								writeToServer(writer);
								
								Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
								Logger.log("Interrupted!");
						}
						if (unresolvedPings >= unresolvedPingLimit) {
								Logger.log(unresolvedPingLimit + " or more unresolved pings.");
								break;
						}
				}
		} catch (IOException e) {
				Logger.log("IOException!");
		}
}

private void writeToServer(PrintWriter writer) {
		while (!queueToServer.isEmpty()) {
				String msg = queueToServer.poll();
				writer.println(msg);
				writer.flush();
				
				Logger.log("Sent \"" + msg + "\"");
		}
}

private boolean readProtocol(BufferedReader reader) throws IOException {
		
		boolean loop = true;
		
		while (reader.ready()) {
				String line = reader.readLine();
				Logger.log("Received \"" + line + "\"");
				StringTokenizer st = new StringTokenizer(line);
				
				switch (st.nextToken()) {
						case Flags.server_terminating:
								loop = false;
								isRunning = false;
								break;
						case Flags.all_backup_servers:
								setBackupServers(line);
								break;
						case Flags.new_backup_server:
								addBackupServer(line);
								break;
						case Flags.ping:
								write(Flags.ping_response);
								break;
						case Flags.ping_response:
								unresolvedPings--;
								break;
						case Flags.new_client:
								write(Flags.client_list);
								break;
						case Flags.client_list:
						
						default:
								queueToUser.offer(line);
								break;
				}
		}
		return loop;
}

private void addBackupServer(String pNewBackupServer) {
		StringTokenizer st = new StringTokenizer(pNewBackupServer);
		if (!Flags.new_backup_server.equals(st.nextToken())) {
				return;
		}
		if (backupServers == null) {
				backupServers = new ArrayList<>();
		}
		while (st.hasMoreTokens()) {
				backupServers.add(createAddress(st));
		}
		String log = "";
		for (Address a : backupServers) {
				log += " " + a.toString();
		}
		Logger.log("Backup servers: " + log);
}

private void setBackupServers(String pList) {
		backupServers = new ArrayList<>();
		addBackupServer(pList);
}

private Address createAddress(StringTokenizer st) {
		String host = st.nextToken();
		int port = Integer.parseInt(st.nextToken());
		int id = Integer.parseInt(st.nextToken());
		
		return new Address(host, port, id);
}
//</editor-fold>
}
