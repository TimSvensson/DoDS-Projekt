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
import DistributedSystem.Server.Server;

import java.io.IOException;
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
		String bannerFormat = "%1$-25s %2$s %1$25s";
		String bannerStr = String.join("", Collections.nCopies(20, "="));
		
		try {
				int wait = 200;
				
				Logger.log(String.format(bannerFormat, bannerStr, "oneClientEchoTest"));
				Logger.log(String.format(resFormat, "RESULTS:", "oneClientEchoTest",
										 results[0] = oneClientEchoTest()));
				Thread.sleep(wait);
				
				Logger.log(String.format(bannerFormat, bannerStr, "twoClientEchoTest"));
				Logger.log(String.format(resFormat, "RESULTS:", "twoClientEchoTest",
										 results[1] = twoClientEchoTest()));
				Thread.sleep(wait);
				
				Logger.log(String.format(bannerFormat, bannerStr, "clientDisconnectTest"));
				Logger.log(String.format(resFormat, "RESULTS:", "clientDisconnectTest",
										 results[2] = clientDisconnectTest()));
				Thread.sleep(wait);
				
				Logger.log(String.format(bannerFormat, bannerStr, "serverTerminationTest"));
				Logger.log(String.format(resFormat, "RESULTS:", "serverTerminationTest",
										 results[3] = serverTerminationTest()));
				Thread.sleep(wait);
				
				Logger.log(String.format(bannerFormat, bannerStr, "backupServerTest"));
				Logger.log(String.format(resFormat, "RESULTS:", "backupServerTest",
										 results[4] = backupServerTest()));
				
				Thread.sleep(wait);
		} catch (InterruptedException pE) {
				pE.printStackTrace();
		}
		
		Logger.log(String.format(bannerFormat, bannerStr, "RESULTS"));
		Logger.log(String.format(resFormat, "RESULTS:", "oneClientEchoTest", results[0]));
		Logger.log(String.format(resFormat, "RESULTS:", "twoClientEchoTest", results[1]));
		Logger.log(String.format(resFormat, "RESULTS:", "clientDisconnectTest", results[2]));
		Logger.log(String.format(resFormat, "RESULTS:", "serverTerminationTest", results[3]));
		Logger.log(String.format(resFormat, "RESULTS:", "backupServerTest", results[4]));
}
//</editor-fold>

//<editor-fold desc="PrivateMethods">
private boolean oneClientEchoTest() {
		//TODO One client sends a msg to one server, all clients receives the msg.
		
		final String host = "localhost";
		final int port = 9001;
		
		Boolean result = true;
		
		Server server = new Server(port);
		Client c1 = new Client(host, port);
		
		server.setup();
		c1.setup();
		
		Logger.log("Sending message from Client to Server.");
		String shout1 = "shout1";
		String shout2 = "shout2";
		c1.write(shout1);
		c1.write(shout2);
		
		Logger.log("Waiting for echo.");
		String echo1 = c1.read();
		if (!echo1.equals(shout1)) {
				result = false;
		}
		Logger.log("echo1 received.");
		
		String echo2 = c1.read();
		if (!echo2.equals(shout2)) {
				result = false;
		}
		Logger.log("echo2 received.");
		
		Logger.log("Terminating...");
		server.terminate();
		
		Logger.log("clientSendTest done.");
		
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
		if (!(shout.read().equals(s1) && shout.read().equals(s2) && shout.read().equals(
			s3))) {
				result = false;
		}
		Logger.log("shout read.");
		
		// Check that echo received the strings in correct order
		if (!(echo.read().equals(s1) && echo.read().equals(s2) && echo.read().equals(s3)
		)) {
				result = false;
		}
		Logger.log("echo read.");
		
		try {
				shout.disconnect();
				echo.disconnect();
		} catch (IOException pE) {
				pE.printStackTrace();
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
		
		try {
				String s1 = "Shout one";
				c3.write(s1);
				
				if (!(c1.read().equals(s1) && c2.read().equals(s1) && c3.read().equals(s1))) {
						return false;
				}
				
				c1.disconnect();
				
				String s2 = "Shout two";
				c3.write(s2);
				
				if (!(c2.read().equals(s2) && c3.read().equals(s2))) {
						return false;
				}
		} catch (IOException pE) {
				pE.printStackTrace();
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
		
		c1.read();
		c2.read();
		
		if (!(c1.isClosed() && c2.isClosed())) {
				return false;
		}
		
		try {
				Thread.sleep(200);
		} catch (InterruptedException pE) {
				pE.printStackTrace();
		}
		
		if (!server.isTerminated()) {
				Logger.log("Server has not terminated.");
				return false;
		}
		
		return true;
}

private boolean backupServerTest() {
		//TODO When a Server crashes, a Backup Server takes over.
		//TODO Check that Clients can communicate.
		
		return false;
}
//</editor-fold>

}