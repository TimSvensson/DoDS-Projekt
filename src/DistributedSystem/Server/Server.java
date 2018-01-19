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
import DistributedSystem.DSUtil;
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
	private boolean activeServer = false;
	private int port;
	private int socketTimeout = 100;
	private final int serverID = DSUtil.createNewIDToken();
	
	private LinkedBlockingQueue<Connection> clients =
		new LinkedBlockingQueue<>();
	private LinkedBlockingQueue<Connection> backupServers =
		new LinkedBlockingQueue<>();
	
	private LinkedBlockingQueue<String> echoQueue = new LinkedBlockingQueue<>();
	
	private Boolean isMainServer = false;
	//</editor-fold>
	
	//<editor-fold desc="Constructors">
	// TODO Add constructor with no port
	public Server() {
		consoleMain();
	}
	
	public Server(int port) {
		this.port = port;
	}
	//</editor-fold>
	
	public static void main(String[] args) {
		new Server();
	}
	
	//<editor-fold desc="PublicMethods">
	public void setup() throws IOException {
		isMainServer = true;
		serverSocket = new ServerSocket(port);
		port = serverSocket.getLocalPort();
		serverSocket.setSoTimeout(socketTimeout);
		
		Thread t = new Thread(this, "server@" + port);
		t.setDaemon(true);
		t.start();
	}
	
	@Override
	public void run() {
		Logger.log("Starting!");
		if (isMainServer) {
			mainServerSetup();
		} else {
			// TODO
		}
		Logger.log("Stopping!");
	}
	
	public void terminate() {
		Logger.log("Terminating server.");
		terminate = true;
		echo(Flags.server_terminating);
	}
	
	public boolean isTerminated() {
		return terminate && (clients == null);
	}
	
	// This will shut down the server incorrectly
	public void crash() {
		int sleepTime = 1000;
		closeAllConnections();
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
	
	//<editor-fold desc="Console">
	
	private void consoleMain() {
		ArrayList<String> options = new ArrayList<>();
		options.add("==== MAIN MENU ====");
		options.add("Start new [S]erver");
		options.add("Start new [B]ackup Server");
		options.add("Print [l]og");
		options.add("[Q]uit");
		
		String expectedInput = "sblq";
		
		Boolean quit = false;
		
		while (! quit) {
			char response = UIGetChar(options, expectedInput);
			switch (response) {
			case 's':
				System.out.println("New Server");
				isMainServer = true;
				consoleServer();
				break;
			case 'b':
				System.out.println("New backup Server");
				isMainServer = false;
				consoleBackupServer();
				break;
			case 'l':
				System.out.println("Info:");
				break;
			case 'q':
				quit = true;
				break;
			default:
				
			}
		}
	}
	
	private void consoleServer() {
		ArrayList<String> options = new ArrayList<>();
		options.add("==== SERVER MENU ====");
		options.add("Print [i]nfo, print [l]og");
		options.add("[S]tart Server, S[t]op Server, Set [P]ort");
		options.add("([C]rash), [Q]uit");
		
		String expectedInput = "ilstpcq";
		
		Boolean quit = false;
		
		while (! quit) {
			char response = UIGetChar(options, expectedInput);
			switch (response) {
			case 'i':
				ArrayList<String> info = new ArrayList<>();
				info.add("==== SERVER INFO ====");
				info.add("active        = " + activeServer);
				info.add("serverID      = " + serverID);
				info.add("isMainServer  = " + isMainServer);
				info.add("timeout       = " + socketTimeout);
				info.add("port          = " + port);
				info.add("#Clients      = " + clients.size());
				info.add("#Backups      = " + backupServers.size());
				info.add("terminate     = " + terminate);
				UIPrint(info);
				break;
			case 'l':
				System.out.println("Log");
				break;
			case 's':
				System.out.println("Starting new Server...");
				activeServer = true;
				try { setup(); } catch (IOException e) {
					Logger.log(e.toString());
					System.out.println("Failed to start new server");
					activeServer = false;
				}
				break;
			case 't':
				System.out.println("Stopping the Server...");
				//TODO Stop server
				//TODO Disconnect clients and backups
				terminate = true;
				activeServer = false;
				break;
			case 'p':
				System.out.println("Setting new Server Port...");
				ArrayList<String> lines = new ArrayList<>();
				lines.add("New port number");
				port = UIGetInt(lines);
				break;
			case 'c':
				System.out.println("Crashing!");
				//TODO Crash server to test reconnection to backup
				break;
			case 'q':
				System.out.println("Quiting...");
				//TODO Disconnect clients and backups properly
				//TODO Exit to main menu
				terminate = true;
				quit = true;
				break;
			default:
				
			}
		}
	}
	
	private void consoleBackupServer() {
		ArrayList<String> options = new ArrayList<>();
		options.add("==== BACKUP SERVER MENU ====");
		options.add("Print [i]nfo");
		options.add("print [l]og");
		options.add("[S]tart Backup Server");
		options.add("S[t]op Backup Server");
		options.add("Set [P]ort");
		options.add("[Q]uit");
		
		String expectedInput = "ilstpcq";
		
		Boolean quit = false;
		
		while (! quit) {
			char response = UIGetChar(options, expectedInput);
			switch (response) {
			case 'i':
				System.out.println("Info:");
				break;
			case 'l':
				System.out.println("Log");
				break;
			case 's':
				System.out.println("Starting new Backup Server...");
				break;
			case 't':
				System.out.println("Stopping the Backup Server...");
				break;
			case 'p':
				System.out.println("Setting Backup Server Port...");
				break;
			case 'q':
				System.out.println("Quiting...");
				terminate = true;
				quit = true;
				break;
			default:
				
			}
		}
	}
	
	private void UIPrint(List<String> lines) {
		for (String s : lines) {
			System.out.println(s);
		}
	}
	
	private char UIGetChar(List<String> options, String expectedInput) {
		UIPrint(options);
		Scanner in = new Scanner(System.in);
		String input;
		do {
			System.out.print("char> ");
			input = in.nextLine();
		} while ( ! expectedInput.contains(input.toLowerCase()) || input.length() != 1);
		
		return input.charAt(0);
	}
	
	// Both bounds are inclusive
	private int UIGetIntInBound(List<String> options, int lowerBound,
								int upperBound) {
		assert lowerBound <= upperBound;
		UIPrint(options);
		Scanner userInput = new Scanner(System.in);
		int input;
		do {
			System.out.println("Upper=" + upperBound + ", lower=" + lowerBound);
			System.out.println("int> ");
			input = userInput.nextInt();
		} while(input < lowerBound || input > upperBound);
		return input;
	}
	
	private int UIGetInt(List<String> options) {
		UIPrint(options);
		Scanner userInput = new Scanner(System.in);
		int input;
		do {
			System.out.println("int> ");
			input = userInput.nextInt();
		} while(false);
		return input;
	}
	
	//</editor-fold>
	
	private void mainServerSetup() {
		
		Logger.log("Entering mainServerSetup().");
		try {
			Logger.log("Main server ServerSocket open.");
			while (!terminate) {
				waitForConnection(serverSocket);
				for (Connection c : clients) {
					read(c);
				}
				echo();
			}
			
			Logger.log("Closing ServerSocket.");
			serverSocket.close();
			
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
				echo(Flags.new_client);
				
			} else if (connectionType.equals(Flags.server_backup)) {
				
				backupServers.add(connection);
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
	
	private void read(Connection client) {
		try {
			Logger.log("Waiting for incoming messages.");
			String s;
			int reads = 10;
			// TODO Implement StringTokenizer on read
			while ((s = client.read()) != null && reads > 0) {
				//Logger.log("Received: " + s);
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
					System.out.println("default echo");
					echo(s);
				}
				reads--;
			}
		} catch (IOException pE) {
			Logger.log("IOException!");
			// pE.printStackTrace();
		}
	}
	
	private void echo() {
		System.out.println("ECHO!!!");
		try {
			while (echoQueue != null) {
				String s = echoQueue.take();
				writeToAll(s);
			}
		} catch (InterruptedException pE) {
			Logger.log("Interrupted!");
		}
	}
	
	private void writeToAll(String s) {
		
		Logger.log("Echoing \"" + s + "\"");
		for (Connection c : clients) {
			c.write(s);
		}
		for (Connection c : backupServers) {
			c.write(s);
		}
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
		return DSUtil.createNewIDToken();
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
	
	//</editor-fold>
}