/*
 * Project: DoDS-Projekt 
 * Class:   Logger
 *
 * Version info
 * Created: 16/06/17
 * Author: Tim Svensson <svensson_tim@hotmail.se>
 */

package DistributedSystem;

import java.io.PrintWriter;
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
public class Logger {

//<editor-fold desc="FieldVariables">
private static boolean fsFirstEntry = true;
private static String fsLogFormat = "%-15s%-25s%s";
private static PrintWriter fsWriter = new PrintWriter(System.out);
//</editor-fold>

//<editor-fold desc="Constructors">
public Logger() {

}
//</editor-fold>

//<editor-fold desc="GettersAndSetters">

//</editor-fold>

//<editor-fold desc="PublicMethods">
public static void log(String pMessage) {
	if (fsFirstEntry) {
		fsFirstEntry = false;
		fsWriter.println(String.format(fsLogFormat, "CRNT SYS TIME", "THREAD", "MESSAGE"));
		
		// create a string made up of n copies of s
		fsWriter.println(String.join("", Collections.nCopies(80, "-")));
	}
	fsWriter.println(String.format(fsLogFormat, System.currentTimeMillis(), Thread.currentThread().getName(),
								   pMessage));
	fsWriter.flush();
}
//</editor-fold>

//<editor-fold desc="PrivateMethods">

//</editor-fold>

}