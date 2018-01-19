/*
 * Project: DoDS-Projekt 
 * Class:   Address
 *
 * Version info
 * Created: 02/08/17
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
public class Address {
	
	private final String address;
	private final int port;
	private final int id;
	
	public Address(String address, int port, int id) {
		this.port = port;
		this.address = address;
		this.id = id;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getID() {
		return id;
	}
	
	@Override
	public String toString() {
		return address + " " + port + " " + id;
	}
}