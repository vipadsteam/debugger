package message;

public interface IAgentLogger {

	public void sendError(Object... obj);

	public void sendWarn(Object... obj);

	public void sendInfo(Object... obj);

}
