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
        try {
            Logger.log(String.format("%1$-10s %2$s %1$10s", "=====", "oneClientEchoTest"));
            Logger.log(String.format("%s %25s %10B", "RESULTS:", "oneClientEchoTest",
                                     oneClientEchoTest()));
            Thread.sleep(100);

            Logger.log(String.format("%1$-10s %2$s %1$10s", "=====", "twoClientEchoTest"));
            Logger.log(String.format("%s %25s %B", "RESULTS:", "twoClientEchoTest",
                                     twoClientEchoTest()));
            Thread.sleep(100);

            Logger.log(String.format("%1$-10s %2$s %1$10s", "=====", "clientDisconnectTest"));
            Logger.log(String.format("%s %25s %B", "RESULTS:", "clientDisconnectTest",
                                     clientDisconnectTest()));
            Thread.sleep(100);

            Logger.log(String.format("%1$-10s %2$s %1$10s", "=====", "serverTerminationTest"));
            Logger.log(String.format("%s %25s %B", "RESULTS:", "serverTerminationTest",
                                     serverTerminationTest()));
            Thread.sleep(100);

            Logger.log(String.format("%1$-10s %2$s %1$10s", "=====", "backupServerTest"));
            Logger.log(String.format("%s %25s %B", "RESULTS:", "backupServerTest",
                                     backupServerTest()));
        } catch (InterruptedException pE) {
            pE.printStackTrace();
        }
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

        Thread serverThread = new Thread(server, "H:" + host + "P:" + port);
        serverThread.setDaemon(true);
        serverThread.start();

        c1.connect();

        Logger.log("Sending message from Client to Server.");
        String shout1 = "shout1";
        String shout2 = "shout2";
        c1.write(shout1);
        c1.write(shout2);

        Logger.log("Waiting for echo.");
        String echo1 = null;
        try {
            echo1 = c1.read();
        } catch (IOException pE) {
            pE.printStackTrace();
        }
        if (!echo1.equals(shout1)) {
            result = false;
        }
        Logger.log("echo1 received.");

        String echo2 = null;
        try {
            echo2 = c1.read();
        } catch (IOException pE) {
            pE.printStackTrace();
        }
        if (!echo2.equals(shout2)) {
            result = false;
        }
        Logger.log("echo2 received.");

        try {
            c1.disconnect();
        } catch (IOException pE) {
            pE.printStackTrace();
        }
        server.terminate();

        Logger.log("clientSendTest done.");

        return result;
    }

    private boolean twoClientEchoTest() {

        boolean result = true;

        String host = "localhost";
        int port = 9002;

        Server s = new Server(port);
        Thread serverThread = new Thread(s, "H:" + host + " P:" + port);
        serverThread.setDaemon(true);
        serverThread.start();

        Client shout = new Client(host, port);
        Client echo = new Client(host, port);

        shout.connect();
        echo.connect();

        String s1 = "first!";
        String s2 = "second!";
        String s3 = "third!";

        shout.write(s1);
        shout.write(s2);
        shout.write(s3);

        try {
            // Check that shout received the strings in correct order
            if (!(shout.read().equals(s1) && shout.read().equals(s2) && shout.read().equals(s3))) {
                result = false;
            }
            Logger.log("shout read.");

            // Check that echo received the strings in correct order
            if (!(echo.read().equals(s1) && echo.read().equals(s2) && echo.read().equals(s3))) {
                result = false;
            }
            Logger.log("echo read.");
        } catch (IOException pE) {
            pE.printStackTrace();
            result = false;
        }

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
        Thread serverThread = new Thread(server, "H:" + host + " P:" + port);
        serverThread.setDaemon(true);
        serverThread.start();

        Client c1 = new Client(host, port);
        Client c2 = new Client(host, port);
        Client c3 = new Client(host, port);

        c1.connect();
        c2.connect();
        c3.connect();

        try {
            String s1 = "Shout one";
            c3.write(s1);

            if (!(c1.read().equals(s1) && c2.read().equals(s1) && c3.read().equals(s1))) {
                return false;
            }

            c1.disconnect();

            String s2 = "Shout two";
            c3.write(s2);

            if(!(c2.read().equals(s2) && c3.read().equals(s2))) {
                return false;
            }
        } catch (IOException pE) {
            pE.printStackTrace();
        }

        return true;
    }

    private boolean serverTerminationTest() {
        //TODO When the Server terminates, all Clients disconnect from the Server correctly.

        return false;
    }

    private boolean backupServerTest() {
        //TODO When a Server crashes, a Backup Server takes over.
        //TODO Check that Clients can communicate.

        return false;
    }
    //</editor-fold>

}