/*
 * Project: DoDS-Projekt 
 * Class:   Server
 *
 * Version info
 * Created: 16/06/17
 * Author: Tim Svensson <svensson_tim@hotmail.se>
 */

package DistributedSystem.Server;

import DistributedSystem.Address;
import DistributedSystem.Flags;
import DistributedSystem.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
private int socketTimeout = 100;
private final int serverID;

private LinkedBlockingQueue<Connection> clients = new LinkedBlockingQueue<>();
private LinkedBlockingQueue<Connection> backupServers = new LinkedBlockingQueue<>();

private LinkedBlockingQueue<String> echoQueue = new LinkedBlockingQueue<>();
private Thread echoThread;

// Not to be accessed through other means than the method this.getNextClient().
private int nextID = 0;
//</editor-fold>

//<editor-fold desc="Constructors">

// TODO Add constructor with no port

public Server(int port) {
	this(port, 0);
}

public Server(int port, int serverID) {

		this.serverID = serverID;
		this.nextID = this.serverID + 1;
		this.port = port;
}

//</editor-fold>

//<editor-fold desc="PublicMethods">
public void setup() throws IOException {
		
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(socketTimeout);
		
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
		echo(Flags.server_terminating);
}

public boolean isTerminated() {
		
		return terminate && (clients == null) && (echoThread == null);
}

// This will shut down the server incorrectly
public void crash() {
		
		int sleepTime = 1000;
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

private void waitForConnection(ServerSocket lss) throws IOException {
		
		try {
				//Logger.log("Waiting for new connection.");
				Connection connection = new Connection(lss.accept(), getNextClientID());
				
				Logger.log("New connection: " + connection.socket.toString());
				String connectionType = connection.read();
				Logger.log("New connection of type \'" + connectionType + "\'.");
				
				if (connectionType.equals(Flags.client)) {
						
						clients.add(connection);
						String threadName = "SD_Client_" + port + "-" + connection.getPort();
						startNewServerDock(connection, threadName);
						
						echo(Flags.new_client);
						
				} else if (connectionType.equals(Flags.server_backup)) {
						
						backupServers.add(connection);
						String threadName = "SD_Backup_" + port + "-" + connection.getPort();
						startNewServerDock(connection, threadName);
						
						echo(Flags.new_backup_server + " " + connection.toString());
						
				} else {
						// Error has occurred
						Logger.log("Unable to identify connection.");
						connection.close();
						return;
				}
				
		} catch (SocketTimeoutException e) {
				//Logger.log("Socket timeout.");
		}
}

private void startNewServerDock(Connection connection, String threadName) {
		
		ServerDock serverDock = new ServerDock(connection);
		Thread t = new Thread(serverDock, threadName);
		t.setDaemon(true);
		t.start();
}

private void echo(String s) {
		
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

//</editor-fold>

//<editor-fold desc="GettersAndSetters">

public Address getServerAddress() {
	return new Address(serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort(), -1);
}

public String getAllClients() {
		
		return getAllConnections(clients);
}

public String getAllBackupServers() {
		
		return getAllConnections(backupServers);
}

private String getAllConnections(LinkedBlockingQueue<Connection> l) {
		
		if (l == null) {
				return null;
		}
		StringBuilder sb = new StringBuilder();
		for (Object o : l) {
				sb.append(o.toString());
				sb.append(" ");
		}
		return sb.toString();
}

private int getNextClientID() {
		
		int id = nextID;
		nextID++;
		return id;
}

//</editor-fold>


//<editor-fold desc="Inner Classes">
private class Connection {
		
		private final int id;
		private Socket socket;
		private PrintWriter writer;
		private BufferedReader reader;
		
		public Connection(Socket pSocket, int pId) throws IOException {
				
				id = pId;
				socket = pSocket;
				writer = new PrintWriter(pSocket.getOutputStream());
				reader = new BufferedReader(new InputStreamReader(pSocket.getInputStream()));
		}
		
		public void write(String s) {
				
				writer.println(s);
				writer.flush();
		}
		
		public String read() throws IOException {
				
				return reader.readLine();
		}
		
		public void close() throws IOException {
				
				writer.close();
				reader.close();
				socket.close();
		}
		
		public int getPort() {
				
				return socket.getPort();
		}
		
		public String getHost() {
				
				return socket.getInetAddress().getHostName();
		}
		
		public int getID() {
				
				return id;
		}
		
		@Override
		public String toString() {
				
				return getHost() + " " + getPort() + " " + getID();
		}
}


private class ServerDock implements Runnable {
		
		private Connection client;
		
		public ServerDock(Connection pClient) {
				
				client = pClient;
		}
		
		@Override
		public void run() {
				
				Logger.log("Starting ServerDock.");
				
				try {
						Logger.log("Waiting for incoming messages.");
						String s;
						// TODO Implement StringTokenizer on read
						while ((s = client.read()) != null) {
								Logger.log("Received: " + s);
								switch (s) {
										case Flags.ping:
												client.write(Flags.ping_response);
												break;
										case Flags.all_backup_servers:
												client.write(Flags.all_backup_servers + " " +
															 getAllBackupServers());
												break;
										case Flags.id:
												client.write(Flags.id + " " + client.getID());
												break;
										case Flags.client:
												Logger.log("Received \"" + Flags.client + "\", " +
														   "something's gone wrong.");
												break;
										case Flags.client_list:
												client.write(
													Flags.client_list + " " + getAllClients());
												break;
										case Flags.disconnect:
												//TODO remove client from list
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
				
				return client.getHost() + " " + client.getPort();
		}
}


private class ServerEcho implements Runnable {
		
		private void writeToAll(String s) {
				
				Logger.log("Echoing \"" + s + "\"");
				for (Connection c : clients) {
						c.write(s);
				}
				for (Connection c : backupServers) {
						c.write(s);
				}
		}		@Override
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
		

}
//</editor-fold>
}