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
private static boolean isFirstEntry = true;
private static String logFormat = "%-15s%-25s%-60s";
private static PrintWriter writer = new PrintWriter(System.out);
//</editor-fold>

//<editor-fold desc="Constructors">
public Logger() {

}
//</editor-fold>

//<editor-fold desc="GettersAndSetters">

//</editor-fold>

//<editor-fold desc="PublicMethods">
public static void log(String pMessage) {
	if (isFirstEntry) {
			isFirstEntry = false;
		writer.println(String.format(logFormat, "CRNT SYS TIME", "THREAD", "MESSAGE"));
		
		// create a string made up of n copies of s
		writer.println(String.join("", Collections.nCopies(100, "-")));
	}
	writer.println(String.format(logFormat, System.currentTimeMillis(),
								 Thread.currentThread().getName(), pMessage));
	writer.flush();
}
//</editor-fold>

//<editor-fold desc="PrivateMethods">

//</editor-fold>

}