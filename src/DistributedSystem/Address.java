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

		private final String fAddress;
		private final int fPort;
		
		public Address(String pAddress, int pPort) {
				fPort = pPort;
				fAddress = pAddress;
		}
		
		public int getPort() {
				return fPort;
		}
		
		public String getAddress() {
				return fAddress;
		}
		
		@Override
		public String toString() {
				return fAddress + ":" + fPort;
		}
}