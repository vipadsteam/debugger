/**
 * 
 */
package agent;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import bean.ClazzInfo;
import message.AgentMessage;
import trigger.ShutdownTrigger;

/**
 * @author USER
 *
 */
public class AgentMain {

	public static void agentmain(String args, Instrumentation inst) throws Exception {
		AgentMessage.sendInfo("agentmain begin Args:", args);
		AgentTransformer rsa = new AgentTransformer();
		inst.addTransformer(rsa, true);
		inst.retransformClasses(ClazzInfo.getClazz());

		// reform 线程
		ExecutorService exec = Executors.newSingleThreadExecutor(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(Thread.currentThread().getThreadGroup(), r, "agent-reform-thread", 0);
				if (t.isDaemon())
					t.setDaemon(false);
				if (t.getPriority() != Thread.NORM_PRIORITY)
					t.setPriority(Thread.NORM_PRIORITY);
				return t;
			}
		});
		exec.execute(new AgentEndThread(rsa, inst));
		exec.shutdown();

		// 钩子
		Runtime.getRuntime().addShutdownHook(new Thread(new AgentEndThread(rsa, inst, true), "agent-shutdown-reform-thread"));
		AgentMessage.sendInfo("agentmain end:");
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
				AgentMessage.sendInfo("agentmain reloading:");
				inst.removeTransformer(rsa);
				inst.retransformClasses(org.apache.commons.lang3.StringUtils.class);
			} catch (Exception e) {
				AgentMessage.sendError("reform exception:", e);
			}
		}
	}
}
