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

import bean.ClazzInfo;
import socket.AgentProtocol;

/**
 * @author USER
 *
 */
public class ServerThread implements Runnable {

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
			} else if("Communicate".equals(temp)){
				os.write(AgentProtocol.communicateToByte(false, 120));
			}else{
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
		ClazzInfo.setClazz(org.apache.commons.lang3.StringUtils.class);
		ClazzInfo.setClazzByte(Files.readAllBytes(Paths.get("StringUtils.class")));
		return AgentProtocol.clazzInfoToByte();
	}

}
