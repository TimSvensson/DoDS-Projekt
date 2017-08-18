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
import DistributedSystem.DSUtil;
import DistributedSystem.Flags;
import DistributedSystem.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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

private int unresolvedPings = 0;
private int unresolvedPingLimit = 6;
private int sleepTime = 100;

private LinkedBlockingQueue<String> queueToUser = new LinkedBlockingQueue<>();
private LinkedBlockingQueue<String> queueToServer = new LinkedBlockingQueue<>();

private ArrayList<Address> backupServers;
private ArrayList<Address> clients = new ArrayList<>();
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
//</editor-fold>

@Override
public String toString() {
		String s = "%s %i %i", host, port, id;
		return s;
}

//<editor-fold desc="PrivateMethods">
private void clientLoop(String host, int port) {
		while (isRunning) {
				// Connect to the server.
				isDisconnected = false;
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
						
						try {
								Thread.sleep(100);
						} catch (InterruptedException e) {
								e.printStackTrace();
						}
				}
		}
}

private void initConnection(PrintWriter writer) {
		writer.println(Flags.client);
		writer.println(Flags.id);
		writer.println(Flags.all_backup_servers);
		writer.println(Flags.client_list);
		writer.flush();
}

private void connect(String host, int port) {
		Logger.log("Connecting to " + host + " " + port);
		unresolvedPings = 0;
		
		try (Socket s = new Socket(host, port); BufferedReader reader = new BufferedReader(
			new InputStreamReader(s.getInputStream())); PrintWriter writer = new PrintWriter(
			s.getOutputStream())) {
				
				Logger.log("Client connected to " + s.toString() + ".");
				initConnection(writer);
				
				while (true) {
						try {
								if (!listen(reader)) {
										break;
								}
								
								writeToServer(writer);
								
								write(Flags.ping);
								unresolvedPings++;
								
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

private boolean listen(BufferedReader reader) throws IOException {
		
		boolean loop = true;
		
		while (reader.ready()) {
				String line = reader.readLine();
				Logger.log("Received \"" + line + "\"");
				StringTokenizer st = new StringTokenizer(line);
				
				if (!st.hasMoreTokens()) {
						continue;
				}
				
				String token = st.nextToken();
				if (!token.startsWith(Flags.prefix)) {
						queueToUser.offer(line);
						continue;
				}
				
				switch (token) {
						case Flags.server_terminating:
								loop = false;
								isRunning = false;
								break;
						case Flags.all_backup_servers:
								backupServers = new ArrayList<>(setAddressList(line));
								break;
						case Flags.new_backup_server:
								write(Flags.all_backup_servers);
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
								clients = new ArrayList<>(setAddressList(line));
								break;
						case Flags.id:
								this.id = Integer.parseInt(st.nextToken());
								break;
						default:
								Logger.log("Unknown flag: " + token);
				}
		}
		return loop;
}

private void addAddressesToList(List<Address> l, String s) {
		ArrayList<Address> a = DSUtil.getListOfAddresses(s);
		if (a != null) {
				l.addAll(a);
		}
}

private List<Address> setAddressList(String s) {
		return DSUtil.getListOfAddresses(s);
}
//</editor-fold>
}
