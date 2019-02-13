package socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Connector {

	private static Socket socket;

	/**
	 * @return the socket
	 */
	public static Socket getSocket() {
		return socket;
	}

	/**
	 * @param socket
	 *            the socket to set
	 */
	public static void setSocket(Socket socket) {
		Connector.socket = socket;
	}

	public static synchronized Socket initSocket(String args) throws Exception {
		if (null == socket || socket.isClosed()) {
			String[] argsArr = args.split(":");
			Connector.socket = new Socket(argsArr[0], Integer.parseInt(argsArr[1]));
		}
		return Connector.socket;
	}

	public static void close() throws IOException {
		if (null == socket) {
			return;
		}
		if (!socket.isClosed()) {
			socket.close();
		}
		socket = null;
	}

	public static final byte[] input2byte(InputStream inStream) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(inStream, output);
		byte[] in2b = output.toByteArray();
		return in2b;
	}

	private static int copy(InputStream input, OutputStream output) throws IOException {
		long count = copyLarge(input, output);
		if (count > 2147483647L) {
			return -1;
		}
		return (int) count;
	}

	private static long copyLarge(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[4096];
		long count = 0L;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
}
