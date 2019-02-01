package message;

public class AgentMessage {

	public static void sendError(Object... obj) {
		System.err.println(obj);
	}

	public static void sendWarn(Object... obj) {
		System.out.println(obj);
	}

	public static void sendInfo(Object... obj) {
		System.out.println(obj);
	}

}
