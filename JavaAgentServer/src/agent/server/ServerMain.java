/**
 * 
 */
package agent.server;

import java.io.IOException;
import java.net.Socket;

/**
 * @author USER
 * 要用回自定义socket才行
 */
public class ServerMain {

	public void connect() {
		try {
			while (true) {
				Socket socket = Server.server.accept();
				// socket.getRemoteSocketAddress();
				new Thread(new ServerThread(socket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		ServerMain ss = new ServerMain();
		ss.connect();
	}
}
