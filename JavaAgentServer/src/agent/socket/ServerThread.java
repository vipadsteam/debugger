/**
 * 
 */
package agent.socket;

import java.net.Socket;

/**
 * @author USER
 *
 */
public class ServerThread extends Thread {
	Socket socket = null;

	public ServerThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		// 服务器处理代码
	}
}
