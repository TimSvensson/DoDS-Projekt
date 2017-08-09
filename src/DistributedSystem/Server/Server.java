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
private boolean fTerminate = false;
private int fPortNumber;

private List<Connection> fClients = Collections.synchronizedList(new ArrayList<Connection>());
private List<Connection> fBackupServers = Collections.synchronizedList(new ArrayList<Connection>());
private LinkedBlockingQueue<String> fEchoQueue = new LinkedBlockingQueue<>();

private Thread fEchoThread;
//</editor-fold>

//<editor-fold desc="Constructors">

// TODO Add constructor with no port

public Server(int pPortNumber) {
		fPortNumber = pPortNumber;
}

//</editor-fold>

//<editor-fold desc="PublicMethods">
public void setup() {
		Thread t = new Thread(this, "server@" + fPortNumber);
		t.setDaemon(true);
		t.start();
		
		try {
				Thread.sleep(100);
		} catch (InterruptedException pE) {
				pE.printStackTrace();
		}
}

@Override
public void run() {
		Logger.log("Starting!");
		mainServerSetup();
		Logger.log("Stopping!");
}

public void terminate() {
		Logger.log("Terminating server.");
		fTerminate = true;
}

public boolean isTerminated() {
		return fTerminate && (fClients == null) && (fEchoThread == null);
}

// This will shut down the server incorrectly
public void stop() {
		int sleepTime = 4000;
		try {
				Thread.sleep(sleepTime);
		} catch (InterruptedException pE) {
				pE.printStackTrace();
		}
		fEchoThread.interrupt();
		closeAllConnections();
		closeServerEcho();
		fClients = null;
		fBackupServers = null;
		fTerminate = true;
}
//</editor-fold>

//<editor-fold desc="PrivateMethods">
private void mainServerSetup() {
		
		fEchoThread = new Thread(new ServerEcho(), "ServerEcho@" + fPortNumber);
		fEchoThread.setDaemon(true);
		fEchoThread.start();
		
		try {
				ServerSocket lSS = new ServerSocket(fPortNumber);
				lSS.setSoTimeout(100);
				
				Logger.log("Main server ServerSocket open.");
				while (!fTerminate) {
						waitForConnection(lSS);
				}
				echo(Flags.server_terminating);
				
				try {
						Thread.sleep(100);
				} catch (InterruptedException pE) {
						pE.printStackTrace();
				}
				
				Logger.log("Closing ServerSocket.");
				lSS.close();
				
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
				Logger.log("Waiting for new connection.");
				Connection connection = new Connection(pLSS.accept());
				
				String connectionType = connection.read();
				String serverDockName = "";
				boolean newBackup = false;
				if (connectionType.equals(Flags.client)) {
						fClients.add(connection);
						serverDockName = "ClientDock@" + fPortNumber + "-" + connection.getPort();
				} else if (connectionType.equals(Flags.server_backup)) {
						newBackup = true;
						fBackupServers.add(connection);
						echo(connection.toString());
						serverDockName = "BackupDock@" + fPortNumber + "-" + connection.getPort();
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
				
				echo(Flags.new_client + " " + connection.getHost() + ":" + connection.getPort());
				
				if (newBackup) {
						echo(getBackupList());
				}
		} catch (SocketTimeoutException pE) {
		}
}

private synchronized void echo(String s) {
		fEchoQueue.offer(s);
}

private void closeAllConnections() {
		synchronized(fClients) {
				Iterator<Connection> iterator = fClients.iterator();
				while (iterator.hasNext()) {
						try {
								iterator.next().close();
						} catch (IOException pE) {
								pE.printStackTrace();
						}
				}
		}
		fClients = null;
		synchronized(fBackupServers) {
				Iterator<Connection> iterator = fBackupServers.iterator();
				while (iterator.hasNext()) {
						try {
								iterator.next().close();
						} catch (IOException pE) {
								pE.printStackTrace();
						}
				}
		}
		fBackupServers = null;
}

private void closeServerEcho() {
		fEchoThread.interrupt();
		try {
				fEchoThread.join();
		} catch (InterruptedException pE) {
				pE.printStackTrace();
		}
		fEchoThread = null;
}

private String getBackupList() {
		String backupLists = Flags.new_backup_server;
		synchronized(fBackupServers) {
				Iterator<Connection> iterator = fBackupServers.iterator();
				while (iterator.hasNext()) {
						Connection c = iterator.next();
						backupLists += " " + c.getHost() + " " + c.getPort();
				}
		}
		return backupLists;
}
//</editor-fold>

//<editor-fold desc="GettersAndSetters">

//</editor-fold>

//<editor-fold desc="Inner Classes">
private class Connection {
		private Socket fSocket;
		private PrintWriter fWriter;
		private BufferedReader fReader;
		
		public Connection(Socket pSocket) throws IOException {
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
								Logger.log("Received: " + s);
								switch (s) {
										case Flags.ping:
												fClient.write(Flags.ping_response);
												break;
										case Flags.all_backup_servers:
												fClient.write(getBackupList());
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
		
		public int getPort() {
				return fClient.getPort();
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
						while (fEchoQueue != null) {
								String s = fEchoQueue.take();
								echo(s);
						}
				} catch (InterruptedException pE) {
						Logger.log("I was interrupted!");
				}
				
				Logger.log("Stopping ServerEcho.");
		}
		
		private void echo(String s) {
				Logger.log("Echoing \"" + s + "\"");
				synchronized(fClients) {
						Iterator<Connection> iterator = fClients.iterator();
						while (iterator.hasNext()) {
								iterator.next().write(s);
						}
				}
				synchronized(fBackupServers) {
						Iterator<Connection> iterator = fBackupServers.iterator();
						while (iterator.hasNext()) {
								iterator.next().write(s);
						}
				}
		}
}
//</editor-fold>
}