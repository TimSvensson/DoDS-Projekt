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
import sun.rmi.runtime.Log;

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
private Address fMainServerAddress;
private int backupPort;
private ArrayList<Address> fBackupServers = new ArrayList<>();
private boolean fRunning = true;

public Server newMainServer;
//</editor-fold>

//<editor-fold desc="Constructors">

public BackupServer(Address pMainServerAddress, int pBackupPort) {
		fMainServerAddress = pMainServerAddress;
		backupPort = pBackupPort;
}

//</editor-fold>

//<editor-fold desc="GettersAndSetters">

//</editor-fold>

//<editor-fold desc="PublicMethods">
@Override
public void run() {
		Logger.log("Starting.");
		while (fRunning) {
				Logger.log("Connecting to main server.");
				try (Socket s = new Socket(fMainServerAddress.getAddress(),
										   fMainServerAddress.getPort());
					 BufferedReader reader = new BufferedReader(
						 new InputStreamReader(s.getInputStream()));
					 PrintWriter writer = new PrintWriter(s.getOutputStream())) {
						
						Logger.log("Connected.");
						
						// Tell the main server who I am
						writer.println(Flags.server_backup);
						writer.flush();
						
						// Request and save the list of backup server
						writer.println(Flags.new_backup_server);
						writer.flush();
						
						waitLoop(reader, writer);
						
				} catch (UnknownHostException pE) {
						pE.printStackTrace();
				} catch (IOException pE) {
						pE.printStackTrace();
				}
				
				if (fBackupServers.isEmpty()) {
						fRunning = false;
				} else if (
					fBackupServers.get(0).getAddress().equals(new Socket().getLocalAddress()) &&
					fBackupServers.get(0).getPort() == backupPort) {
						
						newMainServer = new Server(backupPort);
				} else {
						fMainServerAddress = fBackupServers.get(0);
						fBackupServers.remove(0);
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
//</editor-fold>

//<editor-fold desc="PrivateMethods">
private void waitLoop(BufferedReader pReader, PrintWriter pWriter) {
		
		Logger.log("Entering waitLoop().");
		int unresolvedPings = 0;
		boolean loop = true;
		while (loop) {
				try {
						pWriter.println(Flags.ping);
						pWriter.flush();
						unresolvedPings++;
						
						while (pReader.ready()) {
								String s = pReader.readLine();
								StringTokenizer st = new StringTokenizer(s);
								switch (st.nextToken()) {
										case Flags.ping_response:
												unresolvedPings--;
												break;
										case Flags.server_terminating:
												Logger.log(Flags.server_terminating + " received.");
												loop = false;
												fRunning = false;
												break;
										case Flags.ping:
												pWriter.println(Flags.ping_response);
												break;
										case Flags.new_backup_server:
												Logger.log(Flags.new_backup_server + " received.");
												addBackupServer(s);
												break;
										case Flags.all_backup_servers:
												Logger.log(Flags.all_backup_servers + " received.");
												setBackupServers(s);
												break;
								}
						}
						
						Thread.sleep(100);
				} catch (IOException pE) {
						Logger.log("IOException, main server is down.");
						loop = false;
				} catch (InterruptedException pE) {
						Logger.log("InterruptedException.");
				}
				if (unresolvedPings >= 5) {
						Logger.log("Five or more unresolved pings.");
						loop = false;
				}
		}
		Logger.log("Exiting waitLoop().");
}

private void addBackupServer(String pNewBackupServer) {
		StringTokenizer st = new StringTokenizer(pNewBackupServer);
		if (!Flags.new_backup_server.equals(st.nextToken())) {
				return;
		}
		while (st.hasMoreTokens()) {
				String host = st.nextToken();
				int port = Integer.parseInt(st.nextToken());
				fBackupServers.add(new Address(host, port));
		}
		Logger.log("Backup Servers:" + fBackupServers.toString());
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
		Logger.log("Backup Servers:" + fBackupServers.toString());
		return true;
}
//</editor-fold>
}