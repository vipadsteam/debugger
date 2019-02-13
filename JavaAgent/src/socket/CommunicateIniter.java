/**
 * 
 */
package socket;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

import message.AgentMessage;

/**
 * @author USER
 *
 */
public class CommunicateIniter {

	public static synchronized boolean refresh(String args, boolean demon) throws Exception {
		InputStream is = null;
		Writer writer = null;
		try {
			Socket socket = Connector.initSocket(args);
			writer = new OutputStreamWriter(socket.getOutputStream());
			writer.write("Communicate");
			writer.write(AgentProtocol.EOF);
			writer.flush();
			is = socket.getInputStream();
			byte[] input = Connector.input2byte(is);
			AgentProtocol.byteToCommunicate(input);
			return true;
		} catch (Exception e) {
			AgentMessage.sendError("Communicate refresh Exception: ", e);
		} finally {
			// 4、关闭资源
			try {
				if (null != is) {
					is.close();
				}
				if (null != writer) {
					writer.close();
				}
			} catch (Exception e) {
				AgentMessage.sendError("client close Exception:", e);
			}
		}
		return false;
	}

}
