/*
 * Project: DoDS-Projekt 
 * Class:   DistibutedSystemTest
 *
 * Version info
 * Created: 16/06/17
 * Author: Tim Svensson <svensson_tim@hotmail.se>
 */

package DistributedSystem;

import DistributedSystem.Client.Client;
import DistributedSystem.Server.BackupServer;
import DistributedSystem.Server.Server;

import java.util.Collections;

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
public class DSTest {

//<editor-fold desc="FieldVariables">
//</editor-fold>

//<editor-fold desc="Constructors">
public DSTest() {

}
//</editor-fold>

//<editor-fold desc="GettersAndSetters">
//</editor-fold>

//<editor-fold desc="PublicMethods">
public void runTests() {
		boolean[] results = new boolean[5];
		String resFormat = "%s %25s %10B";
		int lengthOfBanner = 60;
		
		try {
				int wait = 500;
				
				String t1 = "oneClientEchoTest";
				Logger.log(getBanner(t1, lengthOfBanner));
				Logger.log(String.format(resFormat, "RESULTS:", "oneClientEchoTest",
										 results[0] = oneClientEchoTest()));
				Thread.sleep(wait);
				
				String t2 = "twoClientEchoTest";
				Logger.log(getBanner(t2, lengthOfBanner));
				Logger.log(String.format(resFormat, "RESULTS:", "twoClientEchoTest",
										 results[1] = twoClientEchoTest()));
				Thread.sleep(wait);
				
				String t3 = "clientDisconnectTest";
				Logger.log(getBanner(t3, lengthOfBanner));
				Logger.log(String.format(resFormat, "RESULTS:", "clientDisconnectTest",
										 results[2] = clientDisconnectTest()));
				Thread.sleep(wait);
				
				String t4 = "serverTerminationTest";
				Logger.log(getBanner(t4, lengthOfBanner));
				Logger.log(String.format(resFormat, "RESULTS:", "serverTerminationTest",
										 results[3] = serverTerminationTest()));
				Thread.sleep(wait);
				
				String t5 = "backupServerTest";
				Logger.log(getBanner(t5, lengthOfBanner));
				Logger.log(String.format(resFormat, "RESULTS:", "backupServerTest",
										 results[4] = backupServerTest()));
				
				Thread.sleep(wait);
		} catch (InterruptedException pE) {
				pE.printStackTrace();
		}
		
		Logger.log(getBanner("RESULTS", lengthOfBanner));
		Logger.log(String.format(resFormat, "RESULTS:", "oneClientEchoTest", results[0]));
		Logger.log(String.format(resFormat, "RESULTS:", "twoClientEchoTest", results[1]));
		Logger.log(String.format(resFormat, "RESULTS:", "clientDisconnectTest", results[2]));
		Logger.log(String.format(resFormat, "RESULTS:", "serverTerminationTest", results[3]));
		Logger.log(String.format(resFormat, "RESULTS:", "backupServerTest", results[4]));
}
//</editor-fold>

//<editor-fold desc="PrivateMethods">
private String getBanner(String s, int lengthOfBanner) {
		
		String flareChar = "=";
		String whiteSpaceChar = " ";
		int lengthOfString = s.length();
		String banner = "";
		int whiteSpace = 5;
		int flareLength = lengthOfBanner/2 - lengthOfString/2 - whiteSpace;
		
		banner = banner + String.join("", Collections.nCopies(flareLength, flareChar));
		if (lengthOfString % 2 == 0) {
				banner = banner + String.join("", Collections.nCopies(whiteSpace, whiteSpaceChar));
		} else {
				banner = banner + String.join("", Collections.nCopies(whiteSpace-1,
																	  whiteSpaceChar));
		}
		banner = banner + String.join("", s);
		banner = banner + String.join("", Collections.nCopies(whiteSpace, whiteSpaceChar));
		banner = banner + String.join("", Collections.nCopies(flareLength, flareChar));
		
		return banner;
}

private boolean oneClientEchoTest() {
		//TODO One client sends a msg to one server, all clients receives the msg.
		
		final String host = "localhost";
		final int port = 9001;
		
		Boolean result = true;
		
		Server server = new Server(port);
		Client c1 = new Client(host, port);
		
		server.setup();
		c1.setup();
		
		String shout1 = "shout1";
		String shout2 = "shout2";
		c1.write(shout1);
		c1.write(shout2);
		
		String echo1 = c1.read();
		if (!echo1.equals(shout1)) {
				result = false;
		}
		
		String echo2 = c1.read();
		if (!echo2.equals(shout2)) {
				result = false;
		}
		
		server.terminate();
		
		return result;
}

private boolean twoClientEchoTest() {
		
		boolean result = true;
		
		String host = "localhost";
		int port = 9002;
		
		Server s = new Server(port);
		s.setup();
		
		Client shout = new Client(host, port);
		Client echo = new Client(host, port);
		
		shout.setup();
		echo.setup();
		
		String s1 = "first!";
		String s2 = "second!";
		String s3 = "third!";
		
		shout.write(s1);
		shout.write(s2);
		shout.write(s3);

		// Check that shout received the strings in correct order
		if (!(shout.read().equals(s1) && shout.read().equals(s2) && shout.read().equals(s3))) {
				Logger.log("Client shout read wrong.");
				result = false;
		}
		
		// Check that echo received the strings in correct order
		if (!(echo.read().equals(s1) && echo.read().equals(s2) && echo.read().equals(s3)
		)) {
				Logger.log("Client echo read wrong.");
				result = false;
		}
		
		s.terminate();
		
		return result;
}

private boolean clientDisconnectTest() {
		
		String host = "localhost";
		int port = 9003;
		
		Server server = new Server(port);
		server.setup();
		
		Client c1 = new Client(host, port);
		Client c2 = new Client(host, port);
		Client c3 = new Client(host, port);
		
		c1.setup();
		c2.setup();
		c3.setup();
		

		String s1 = "Shout one";
		c3.write(s1);
		
		if (!(c1.read().equals(s1) && c2.read().equals(s1) && c3.read().equals(s1))) {
				Logger.log("String s1 misread.");
				return false;
		}
		
		c1.disconnect();
		
		String s2 = "Shout two";
		c3.write(s2);
		
		if (!(c2.read().equals(s2) && c3.read().equals(s2))) {
				Logger.log("String s2 misread.");
				return false;
		}
		
		server.terminate();
		
		
		return true;
}

private boolean serverTerminationTest() {
		String host = "localhost";
		int port = 9004;
		
		Client c1 = new Client(host, port);
		Client c2 = new Client(host, port);
		
		Server server = new Server(port);
		server.setup();
		
		c1.setup();
		c2.setup();
		
		String test = "Check";
		c1.write(test);
		
		String c1Echo = c1.read();
		String c2Echo = c2.read();
		
		if (!(test.equals(c1Echo) && test.equals(c2Echo))) {
				Logger.log("Sanity check failed.");
				return false;
		}
		
		server.terminate();
		
		if (!(c1.isDisconnected() && c2.isDisconnected())) {
				Logger.log("c1 or c2 has not disconnected properly.");
				return false;
		}
		
		if (!server.isTerminated()) {
				Logger.log("Server has not terminated.");
				return false;
		}
		
		return true;
}

private boolean backupServerTest() {
		boolean result = false;
		//TODO When a Server crashes, a Backup Server takes over.
		//TODO Check that Clients can communicate.
		String host = "localhost";
		int port = 9005;
		int bsPort1 = 9006;
		int bsPort2 = 9007;
		
		Server ms = new Server(port);
		BackupServer bs1 = new BackupServer(new Address(host, port, -1), bsPort1);
		BackupServer bs2 = new BackupServer(new Address(host, port, -1), bsPort2);
		
		Client c1 = new Client(host, port);
		Client c2 = new Client(host, port);
		
		ms.setup();
		bs1.setup();
		bs2.setup();
		
		c1.setup();
		c2.setup();
		
		Logger.log("TEST: Checking communication.");
		String test1 = "test1";
		c1.write(test1);
		
		String c1Resp1 = c1.read();
		String c2Resp1 = c2.read();
		
		Logger.log("TEST: c1Resp: " + c1Resp1);
		Logger.log("TEST: c2Resp: " + c2Resp1);
		
		// Crashes the main server
		Logger.log("TEST: Stopping main server.");
		ms.stop();
		
		// TODO Check to see if backupServer takes over
		
		// TODO Check if clients still can communicate
		Logger.log("TEST: Checking communication.");
		String test2 = "test2";
		c1.write(test2);
		
		String c1Resp2 = c1.read();
		String c2Resp2 = c2.read();
		
		Logger.log("TEST: c1Resp: " + c1Resp2);
		Logger.log("TEST: c2Resp: " + c2Resp2);
		
		if (c1Resp2.equals(test2) && c2Resp2.equals(test2)) {
				result = true;
		}
		
		ms.terminate();
		bs1.newMainServer.terminate();
		bs1.terminate();
		bs2.terminate();
		
		return result;
}
//</editor-fold>

}