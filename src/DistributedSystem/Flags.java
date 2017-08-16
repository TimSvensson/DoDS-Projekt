/*
 * Project: DoDS-Projekt 
 * Class:   Flags
 *
 * Version info
 * Created: 03/08/17
 * Author: Tim Svensson <svensson_tim@hotmail.se>
 */

package DistributedSystem;

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
public class Flags {

public static final String prefix = "##";

public static final String client = prefix + "client";
public static final String new_client = prefix + "new_client";
public static final String server_main = prefix + "server_main";
public static final String server_backup = prefix + "server_backup";
public static final String ping = prefix + "ping";
public static final String ping_response = prefix + "ping_response";
public static final String server_terminating = prefix + "server_terminating";
public static final String disconnect = prefix + "disconnect";
public static final String new_backup_server = prefix + "new_backup_server";
public static final String all_backup_servers = prefix + "all_backup_servers";
public static final String id = prefix + "id";
public static final String client_list = prefix + "client_list";

}