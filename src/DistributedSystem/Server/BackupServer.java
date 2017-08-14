/*
 * Project: DoDS-Projekt 
 * Class:   BackupServer
 *
 * Version info
 * Created: 02/08/17
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
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
public class BackupServer implements Runnable {

//<editor-fold desc="FieldVariables">
public Server newMainServer = null;
int unresolvedPings = 0;
int unresolvedPingLimit = 4;
private Address mainServerAddress;
private int backupPort;
private int id;
private ArrayList<Address> backupServers = new ArrayList<>();

private boolean isRunning = true;
//</editor-fold>

//<editor-fold desc="Constructors">

public BackupServer(Address pMainServerAddress, int pBackupPort) {
		mainServerAddress = pMainServerAddress;
		backupPort = pBackupPort;
}

//</editor-fold>

//<editor-fold desc="PublicMethods">
@Override
public void run() {
		Logger.log("Starting.");
		while (isRunning) {
				Logger.log("Connecting to main server.");
				try (Socket s = new Socket(mainServerAddress.getAddress(),
										   mainServerAddress.getPort());
					 BufferedReader reader = new BufferedReader(
						 new InputStreamReader(s.getInputStream()));
					 PrintWriter writer = new PrintWriter(s.getOutputStream())) {
						backupPort = s.getLocalPort();
						
						Logger.log("Connected to " + s.toString());
						
						// Tell the main server who I am
						writer.println(Flags.server_backup);
						
						writer.println(Flags.id);
						
						// Request and save the list of backup server
						writer.println(Flags.all_backup_servers);
						writer.flush();
						
						waitLoop(reader, writer);
						
				} catch (UnknownHostException pE) {
						pE.printStackTrace();
				} catch (IOException pE) {
						pE.printStackTrace();
				}
				
				if (backupServers.isEmpty()) {
						isRunning = false;
				} else if (backupServers.get(0).getID() == id) {
						newMainServer = new Server(backupPort);
						newMainServer.setup();
						isRunning = false;
				} else {
						mainServerAddress = backupServers.get(0);
						backupServers.remove(0);
				}
		}
		Logger.log("Stopping.");
}

public void setup() {
		Thread t = new Thread(this);
		t.setName("BServer-" + t.getId());
		t.setDaemon(true);
		t.start();
}

public void terminate() {
		isRunning = false;
}
//</editor-fold>

//<editor-fold desc="PrivateMethods">
private void waitLoop(BufferedReader reader, PrintWriter writer) {
		
		Logger.log("Entering waitLoop().");
		
		boolean loop = true;
		while (loop) {
				try {
						writer.println(Flags.ping);
						writer.flush();
						unresolvedPings++;
						
						loop = protocol(reader, writer);
						
						Thread.sleep(100);
				} catch (IOException pE) {
						Logger.log("IOException, main server is down.");
						loop = false;
				} catch (InterruptedException pE) {
						Logger.log("InterruptedException.");
				}
				if (unresolvedPings >= unresolvedPingLimit) {
						Logger.log(unresolvedPingLimit + " or more unresolved pings.");
						loop = false;
				}
		}
		Logger.log("Exiting waitLoop().");
}

private boolean protocol(BufferedReader reader, PrintWriter writer) throws IOException {
		
		boolean loop = true;
		
		while (reader.ready()) {
				String s = reader.readLine();
				StringTokenizer st = new StringTokenizer(s);
				switch (st.nextToken()) {
						case Flags.ping_response:
								unresolvedPings--;
								break;
						case Flags.server_terminating:
								Logger.log(Flags.server_terminating + " received.");
								loop = false;
								isRunning = false;
								break;
						case Flags.ping:
								writer.println(Flags.ping_response);
								break;
						case Flags.new_backup_server:
								addBackupServer(s);
								break;
						case Flags.all_backup_servers:
								setBackupServers(s);
								break;
						case Flags.id:
								id = Integer.parseInt(st.nextToken());
								Logger.log("My ID: " + id);
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