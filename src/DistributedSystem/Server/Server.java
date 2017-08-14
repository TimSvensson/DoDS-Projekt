/*
 * Project: DoDS-Projekt 
 * Class:   Server
 *
 * Version info
 * Created: 16/06/17
 * Author: Tim Svensson <svensson_tim@hotmail.se>
 */

package DistributedSystem.Server;

import DistributedSystem.Flags;
import DistributedSystem.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;
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

//<editor-fold desc="FieldVariables">
private boolean terminate = false;

private ServerSocket serverSocket;
private int port;

private List<Connection> clients = Collections.synchronizedList(new ArrayList<Connection>());
private List<Connection> backupServers = Collections.synchronizedList(new ArrayList<Connection>());

private LinkedBlockingQueue<String> echoQueue = new LinkedBlockingQueue<>();
private Thread echoThread;

private int nextID = 0;
//</editor-fold>

//<editor-fold desc="Constructors">

// TODO Add constructor with no port

public Server(int pPortNumber) {
		port = pPortNumber;
}

//</editor-fold>



//<editor-fold desc="PublicMethods">
public void setup() {
		
		try {
				serverSocket = new ServerSocket(port);
				serverSocket.setSoTimeout(100);
		} catch (SocketException e) {
				e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		}
		
		Thread t = new Thread(this, "server@" + port);
		t.setDaemon(true);
		t.start();
}

@Override
public void run() {
		Logger.log("Starting!");
		mainServerSetup();
		Logger.log("Stopping!");
}

public void terminate() {
		Logger.log("Terminating server.");
		terminate = true;
}

public boolean isTerminated() {
		return terminate && (clients == null) && (echoThread == null);
}

// This will shut down the server incorrectly
public void stop() {
		int sleepTime = 4000;
		closeAllConnections();
		closeServerEcho();
		clients = null;
		backupServers = null;
		terminate = true;
		try {
				Thread.sleep(sleepTime);
		} catch (InterruptedException pE) {
				pE.printStackTrace();
		}
}
//</editor-fold>

//<editor-fold desc="PrivateMethods">

private void mainServerSetup() {
		
		Logger.log("Entering mainServerSetup().");
		
		echoThread = new Thread(new ServerEcho(), "ServerEcho@" + port);
		echoThread.setDaemon(true);
		echoThread.start();
		
		try {
				Logger.log("Main server ServerSocket open.");
				while (!terminate) {
						waitForConnection(serverSocket);
				}
				echo(Flags.server_terminating);
				
				try {
						Thread.sleep(100);
				} catch (InterruptedException pE) {
						pE.printStackTrace();
				}
				
				Logger.log("Closing ServerSocket.");
				serverSocket.close();
				
				Logger.log("Closing ServerEcho.");
				closeServerEcho();
				
				Logger.log("Closing all Connected Sockets.");
				closeAllConnections();
				
		} catch (IOException pE) {
				pE.printStackTrace();
		}
}

private void waitForConnection(ServerSocket pLSS) throws IOException {
		try {
				//Logger.log("Waiting for new connection.");
				Connection connection = new Connection(pLSS.accept(), getNextClientID());
				
				String connectionType = connection.read();
				Logger.log("New connection of type \'" + connectionType + "\'.");
				String serverDockName;
				if (connectionType.equals(Flags.client)) {
						clients.add(connection);
						//echo(Flags.new_client + " " + connection.toString());
						serverDockName = "ClientDock@" + port + "-" + connection.getPort();
				} else if (connectionType.equals(Flags.server_backup)) {
						backupServers.add(connection);
						//echo(Flags.new_backup_server + " " + connection.toString());
						serverDockName = "BackupDock@" + port + "-" + connection.getPort();
				} else {
						// Error has occurred
						Logger.log("Unable to identify connection.");
						connection.close();
						return;
				}
				
				ServerDock serverDock = new ServerDock(connection);
				Thread t = new Thread(serverDock, serverDockName);
				t.setDaemon(true);
				t.start();
				
		} catch (SocketTimeoutException pE) {
				//Logger.log("Socket timeout.");
		}
}

private synchronized void echo(String s) {
		echoQueue.offer(s);
}

private void closeAllConnections() {
		if (clients != null) {
				synchronized (clients) {
						Iterator<Connection> iterator = clients.iterator();
						while (iterator.hasNext()) {
								try {
										iterator.next().close();
								} catch (IOException pE) {
										pE.printStackTrace();
								}
						}
				}
				clients = null;
		}
		if (backupServers != null) {
				synchronized (backupServers) {
						Iterator<Connection> iterator = backupServers.iterator();
						while (iterator.hasNext()) {
								try {
										iterator.next().close();
								} catch (IOException pE) {
										pE.printStackTrace();
								}
						}
				}
				backupServers = null;
		}
}

private void closeServerEcho() {
		if (echoThread == null) {
				return;
		}
		echoThread.interrupt();
		try {
				echoThread.join();
		} catch (InterruptedException pE) {
				pE.printStackTrace();
		}
		echoThread = null;
}

private String getBackupList() {
		String backupLists = Flags.new_backup_server;
		synchronized(backupServers) {
				Iterator<Connection> iterator = backupServers.iterator();
				while (iterator.hasNext()) {
						Connection c = iterator.next();
						backupLists = backupLists + " " + c.toString();
				}
		}
		return backupLists;
}

private int getNextClientID() {
		int tmp = nextID;
		nextID++;
		return tmp;
		
}

private String getAll(List<Connection> l) {
		String s = "";
		for (Connection c : l) {
				s = s + " " + c.toString();
		}
		return s;
}

//</editor-fold>

//<editor-fold desc="GettersAndSetters">

public String getAllClients() {
		return getAll(clients);
}

public String getAllBackupservers() {
		return getAll(backupServers);
}

//</editor-fold>

//<editor-fold desc="Inner Classes">
private class Connection {
		private Socket fSocket;
		private PrintWriter fWriter;
		private BufferedReader fReader;
		private final int fID;
		
		public Connection(Socket pSocket, int pId) throws IOException {
				fID = pId;
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
		
		public int getPort() {
				return fSocket.getPort();
		}
		
		public String getHost() {
				return fSocket.getInetAddress().getHostName();
		}
		
		public int getID() {
				return fID;
		}
		
		@Override
		public String toString() {
				return getHost() + " " + getPort() + " " + getID();
		}
}

private class ServerDock implements Runnable {
		
		private Connection fClient;
		
		public ServerDock(Connection pClient) {
				fClient = pClient;
		}
		
		@Override
		public void run() {
				Logger.log("Starting ServerDock.");
				
				try {
						Logger.log("Waiting for incoming messages.");
						String s;
						while ((s = fClient.read()) != null) {
								switch (s) {
										case Flags.ping:
												fClient.write(Flags.ping_response);
												break;
										case Flags.all_backup_servers:
												fClient.write(getBackupList());
												break;
										case Flags.id:
												fClient.write(Flags.id + fClient.getID());
												break;
										case Flags.client:
												break;
										default:
												echo(s);
								}
						}
				} catch (IOException pE) {
						Logger.log("IOException!");
						// pE.printStackTrace();
				}
				
				Logger.log("Closing ServerDock.");
		}
		
		@Override
		public String toString() {
				return fClient.getHost() + " " + fClient.getPort();
		}
}

private class ServerEcho implements Runnable {
		
		@Override
		public void run() {
				
				Logger.log("Starting ServerEcho.");
				
				try {
						while (echoQueue != null) {
								String s = echoQueue.take();
								writeToAll(s);
						}
				} catch (InterruptedException pE) {
						Logger.log("Interrupted!");
				}
				
				Logger.log("Stopping ServerEcho.");
		}
		
		private void writeToAll(String s) {
				Logger.log("Echoing \"" + s + "\"");
				synchronized(clients) {
						Iterator<Connection> iterator = clients.iterator();
						while (iterator.hasNext()) {
								iterator.next().write(s);
						}
				}
				synchronized(backupServers) {
						Iterator<Connection> iterator = backupServers.iterator();
						while (iterator.hasNext()) {
								iterator.next().write(s);
						}
				}
		}
}
//</editor-fold>
}