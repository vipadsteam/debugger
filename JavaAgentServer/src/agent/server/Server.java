/**
 * 
 */
package agent.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author USER
 *
 */
public class Server {

	public static ServerSocket server = null;

	static {
		try {
			server = new ServerSocket(10086);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void stop() {
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
