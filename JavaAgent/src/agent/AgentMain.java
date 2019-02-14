/**
 * 
 */
package agent;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import bean.ClazzInfo;
import message.AgentMessage;
import socket.ClassInfoIniter;
import socket.CommunicateIniter;
import socket.Connector;
import trigger.ShutdownTrigger;

/**
 * @author USER
 *
 */
public class AgentMain {

	private static AtomicBoolean isFinished = new AtomicBoolean(false);

	public static void agentmain(String args, Instrumentation inst) throws Exception {
		isFinished.set(false);
		// 验证参数合法性
		if (null == args || args.length() == 0 || args.split(":").length != 2 || !isNumeric(args.split(":")[1])) {
			AgentMessage.sendError("agentmain args is illegal:", args);
			return;
		}

		// 通信握手
		if (!CommunicateIniter.refresh(args)) {
			AgentMessage.sendError("first communicate fail:", args);
			return;
		}

		AgentMessage.sendInfo("agentmain begin Args:", args);

		if (!ClassInfoIniter.init(args)) {
			AgentMessage.sendError("ClassInfo init fail:", args);
		}

		AgentTransformer rsa = new AgentTransformer();
		inst.addTransformer(rsa, true);
		inst.retransformClasses(ClazzInfo.getClazz());

		// 启动通信线程
		startCommunicationThread(args);
		// 启动关闭线程
		startAgentEndThread(rsa, inst);

		// 钩子
		Runtime.getRuntime().addShutdownHook(new Thread(new AgentEndThread(rsa, inst, true), "agent-shutdown-reform-thread"));
		AgentMessage.sendInfo("agentmain end:");
	}

	public static boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(str.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	private static void startAgentEndThread(AgentTransformer rsa, Instrumentation inst) {
		// reform 线程
		ExecutorService exec = Executors.newSingleThreadExecutor(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(Thread.currentThread().getThreadGroup(), r, "agent-reform-thread", 0);
			}
		});
		exec.execute(new AgentEndThread(rsa, inst));
		exec.shutdown();
	}

	private static void startCommunicationThread(String args) {
		// communication 线程
		ExecutorService exec = Executors.newSingleThreadExecutor(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(Thread.currentThread().getThreadGroup(), r, "agent-communi-thread", 0);
			}
		});
		exec.execute(new AgentCommunicationThread(args));
		exec.shutdown();
	}

	/**
	 * @author USER reform 线程
	 */
	static class AgentEndThread implements Runnable {

		private AgentTransformer rsa;

		private Instrumentation inst;

		private boolean force = false;

		public AgentEndThread(AgentTransformer rsa, Instrumentation inst) {
			super();
			this.rsa = rsa;
			this.inst = inst;
		}

		public AgentEndThread(AgentTransformer rsa, Instrumentation inst, boolean force) {
			super();
			this.rsa = rsa;
			this.inst = inst;
			this.force = force;
		}

		@Override
		public void run() {
			try {
				if (!force) {
					ShutdownTrigger.shutdownWait();
				}
				// 设置完成标志
				isFinished.set(true);
				// 关闭socket
				Connector.close();
				AgentMessage.sendInfo("agentmain reloading:");
				inst.removeTransformer(rsa);
				inst.retransformClasses(ClazzInfo.getClazz());
			} catch (Exception e) {
				AgentMessage.sendError("reform exception:", e);
			}
		}
	}

	/**
	 * @author USER communication 线程
	 */
	static class AgentCommunicationThread implements Runnable {

		private String args;

		public AgentCommunicationThread(String args) {
			this.args = args;
		}

		@Override
		public void run() {
			while (true) {
				try {
					// 已经做完了就退出通信线程
					if (isFinished.get()) {
						Connector.close();
						return;
					}
					CommunicateIniter.refresh(args);
				} catch (Throwable t) {
					AgentMessage.sendError("communi exception:", t);
				}
				// sleep
				try {
					// 已经做完了就退出通信线程
					if (isFinished.get()) {
						Connector.close();
						return;
					}
					Thread.sleep(3000);
				} catch (Exception e) {
					AgentMessage.sendError("communi sleep exception:", e);
				}
			}
		}

	}
}
