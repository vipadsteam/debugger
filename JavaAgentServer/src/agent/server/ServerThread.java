/**
 * 
 */
package agent.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import socket.AgentProtocol;

/**
 * @author USER
 *
 */
public class ServerThread implements Runnable {

	private static final String CN = "classname=";

	private static final String PATH = "path=";

	private static final String STOP = "stop=";

	private static final String WAIT = "wait=";

	private Socket socket;

	public ServerThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		BufferedReader is = null;
		OutputStream os = null;
		if (socket == null) {
			System.err.println("Server down!");
			return;
		}
		try {
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String temp;
			temp = is.readLine();
			System.out.println("line is: " + temp);
			os = socket.getOutputStream();
			if ("ClassInfo".equals(temp)) {
				os.write(getClazzInfoByte());
			} else if ("Communicate".equals(temp)) {
				List<String> lines = Files.readAllLines(Paths.get("config"));
				boolean isStop = false;
				int second = 10;
				for (String line : lines) {
					if (line.startsWith(STOP)) {
						isStop = "1".equals(line.substring(STOP.length()));
					} else if (line.startsWith(WAIT)) {
						second = Integer.valueOf(line.substring(WAIT.length()));
					}
				}
				os.write(AgentProtocol.communicateToByte(isStop, second));
			} else {
				os.write("error".getBytes(Charset.forName("UTF-8")));
			}
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private byte[] getClazzInfoByte() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("config"));
		String className = "";
		String path = "";
		for (String line : lines) {
			if (line.startsWith(CN)) {
				className = line.substring(CN.length());
			} else if (line.startsWith(PATH)) {
				path = line.substring(PATH.length());
			}
		}
		return AgentProtocol.clazzInfoToByte(className, Files.readAllBytes(Paths.get(path)));
	}

}
