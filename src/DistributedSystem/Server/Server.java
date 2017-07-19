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
import java.net.SocketException;
import java.net.SocketTimeoutException;
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

//<editor-fold desc="FieldVariables">
private boolean fTerminate = false;
private int fPortNumber;
private ArrayList<ClientSocket> fClients = new ArrayList<>();
private LinkedBlockingQueue<String> fIncomingQueue = new LinkedBlockingQueue<>();
private Thread fEchoThread;
//</editor-fold>

//<editor-fold desc="Constructors">
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
		
		fEchoThread = new Thread(new ServerEcho(), "SE");
		fEchoThread.setDaemon(true);
		fEchoThread.start();
		
		try {
				ServerSocket lSS = new ServerSocket(fPortNumber);
				lSS.setSoTimeout(100);
		
				Logger.log("New ServerSocket created.");
				
				while (!fTerminate) {
						try {
								Logger.log("Waiting for new connection.");
								ClientSocket client = new ClientSocket(lSS.accept());
								fClients.add(client);
								ServerDock serverDock = new ServerDock(client);
								
								Thread t = new Thread(serverDock);
								t.setDaemon(true);
								t.start();
								
								Logger.log("New Client accepted.");
						} catch (SocketTimeoutException pE) {}
				}
				offerToIncomingQueue("##server_terminating");
				
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
		
		Logger.log("Stopping!");
}

public void terminate() {
		Logger.log("Terminating server.");
		fTerminate = true;
}

public boolean isTerminated() {
		return fTerminate && (fClients == null) && (fEchoThread == null);
}
//</editor-fold>

//<editor-fold desc="PrivateMethods">
private synchronized void offerToIncomingQueue(String s) {
		fIncomingQueue.offer(s);
}

private void closeAllConnections() {
		for (ClientSocket cs : fClients) {
				try {
						cs.close();
				} catch (IOException pE) {
						pE.printStackTrace();
				}
		}
		fClients = null;
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
//</editor-fold>

//<editor-fold desc="GettersAndSetters">

//</editor-fold>

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
				Logger.log("Starting ServerDock.");
				
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
				
				Logger.log("Closing ServerDock.");
		}
}

private class ServerEcho implements Runnable {
		
		@Override
		public void run() {
				
				Logger.log("Starting ServerEcho.");
				
				try {
						while (fIncomingQueue != null) {
								String s = fIncomingQueue.take();
								echo(s);
						}
				} catch (InterruptedException pE) {
						Logger.log("I was interrupted!");
				}
				
				Logger.log("Stopping ServerEcho.");
		}
		
		private void echo(String s) {
				Logger.log("Echoing \"" + s + "\"");
				for (ClientSocket client : fClients) {
						client.write(s);
				}
		}
}
}