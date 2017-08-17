/*
 * Project: DoDS-Projekt 
 * Class:   Util
 *
 * Version info
 * Created: 16/08/17
 * Author: Tim Svensson <svensson_tim@hotmail.se>
 */

package DistributedSystem;

import java.util.ArrayList;
import java.util.List;
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
public class DSUtil {

public static ArrayList<Address> getListOfAddresses(String s) {
		
		StringTokenizer st = new StringTokenizer(s);
		String flag = st.nextToken();
		if (!flag.equals(Flags.all_backup_servers) &&
			!flag.equals(Flags.new_backup_server)) {
				return null;
		}
		
		ArrayList<Address> list = new ArrayList<>();
		while (st.hasMoreTokens()) {
				list.add(createAddress(st));
		}
		
		return list;
}

public static Address createAddress(StringTokenizer st) {
		
		String host = st.nextToken();
		int port = Integer.parseInt(st.nextToken());
		int id = Integer.parseInt(st.nextToken());
		
		return new Address(host, port, id);
}

// Taken from
// https://stackoverflow.com/questions/599161/best-way-to-convert-an-arraylist-to-a-string
public static String listToString(List l) {
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
		
}