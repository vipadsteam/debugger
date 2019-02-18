/**
 * 
 */
package agent.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author USER
 *
 */
public class Server {

	public static ServerSocket server = null;

	static {
		try {
			String portStr = Files.readAllLines(Paths.get("port")).get(0);
			server = new ServerSocket(Integer.valueOf(portStr));
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
