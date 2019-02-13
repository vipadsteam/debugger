/**
 * 
 */
package socket;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

import bean.ClazzInfo;
import message.AgentMessage;

/**
 * @author USER
 *
 */
public class ClassInfoIniter {

	public static boolean init(String args) {
		InputStream is = null;
		Writer writer = null;
		try {
			Socket socket = Connector.initSocket(args);
			writer = new OutputStreamWriter(socket.getOutputStream());
			writer.write("ClassInfo");
			writer.write(AgentProtocol.EOF);
			writer.flush();
			// 获取输入流，并读取服务器端的响应信息
			is = socket.getInputStream();
			byte[] input = Connector.input2byte(is);
			AgentProtocol.byteToClazzInfo(input);
			return true;
		} catch (Exception e) {
			AgentMessage.sendError("ClassInfo init Exception: ", e);
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

	public static void main(String[] args) {
		init("localhost:10086");
		System.out.println(ClazzInfo.getClazz().getSimpleName());
		System.out.println(ClazzInfo.getClazzByte().length);
	}

}
