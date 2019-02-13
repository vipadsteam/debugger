package message;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AgentMessage {

	private static IAgentLogger logger;

	public static void sendError(Object... objs) {
		if (null == logger) {
			System.err.println(objs2Str(objs));
		} else {
			logger.sendInfo(objs);
		}
	}

	public static void sendWarn(Object... objs) {
		if (null == logger) {
			System.out.println(objs2Str(objs));
		} else {
			logger.sendWarn(objs);
		}
	}

	public static void sendInfo(Object... objs) {
		if (null == logger) {
			System.out.println(objs2Str(objs));
		} else {
			logger.sendInfo(objs);
		}
	}

	/**
	 * @return the logger
	 */
	public static IAgentLogger getLogger() {
		return logger;
	}

	/**
	 * @param logger
	 *            the logger to set
	 */
	public static void setLogger(IAgentLogger logger) {
		AgentMessage.logger = logger;
	}

	private static String objs2Str(Object[] objs) {
		StringBuffer result = new StringBuffer();
		for (Object obj : objs) {
			result.append(obj2Str(obj));
			result.append("|");
		}
		return result.toString();
	}

	private static String obj2Str(Object obj) {
		if (obj instanceof Throwable) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			((Throwable) obj).printStackTrace(pw);
			return sw.toString();
		}
		return obj.toString();
	}
}
