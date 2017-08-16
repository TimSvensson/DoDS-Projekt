/*
 * Project: DoDS-Projekt 
 * Class:   Logger
 *
 * Version info
 * Created: 16/06/17
 * Author: Tim Svensson <svensson_tim@hotmail.se>
 */

package DistributedSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
private static String pathToFile;
private static String pathToFolder;
private static PrintWriter writer;
//</editor-fold>

//<editor-fold desc="Constructors">
public Logger() {

}
//</editor-fold>

//<editor-fold desc="GettersAndSetters">

//</editor-fold>

//<editor-fold desc="PublicMethods">
public static void log(String pMessage) {
		if (writer == null) {
				try {
						pathToFile = getWorkingDirectory();
						
						File dir = new File(pathToFolder);
						dir.mkdirs();
						
						File file = new File(pathToFile);
						file.createNewFile();
						
						writer = new PrintWriter(file);
				} catch (FileNotFoundException e) {
						e.printStackTrace();
				} catch (IOException e) {
						e.printStackTrace();
				}
		}
		
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
private static String getWorkingDirectory() {
		
		String path;
		String folder = "/DoDS-projekt/log/";
		String fileName = "DoDS-log";
		
		// Code taken from
		// http://stackoverflow.com/questions/11113974/what-is-the-cross-platform-way-of-obtaining
		// -the-pathToFile-to-the-local-application-da
		
		//here, we assign the name of the OS, according to Java, to a variable...
		String OS = (System.getProperty("os.name")).toUpperCase();
		
		//to determine what the workingDirectory is.
		//if it is some version of Windows
		if (OS.contains("WIN")) {
				//it is simply the location of the "AppData" folder
				path = System.getenv("AppData");
		}
		
		//Otherwise, we assume Linux or Mac
		else {
				//in either case, we would start in the user's home directory
				path = System.getProperty("user.home");
				//if we are on a Mac, we are not done, we look for "Application Support"
				path += "/Library";
		}
		
		// Code snippet taken from
		// https://www.mkyong.com/java/java-how-to-get-current-date-time-date-and-calender/
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
		LocalDateTime now = LocalDateTime.now();
		String time = dtf.format(now);
		
		path += folder;
		pathToFolder = path;
		return path + fileName + "_" + time + ".txt";
}
//</editor-fold>

}