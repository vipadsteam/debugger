/**
 * 
 */
package agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import beans.ClazzInfo;
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
			return;
		}

		Set<Class> clazzList = getClasses(ClazzInfo.getClazz());
		if (null == clazzList || clazzList.size() == 0) {
			AgentMessage.sendError("class not founded:", clazzList);
			return;
		}

		AgentMessage.sendInfo("class founded:", ClazzInfo.getClazz(), " size:", clazzList.size());
		printClazzList(clazzList);

		Class[] clazzes = new Class[clazzList.size()];
		int i = 0;
		for (Class clazz : clazzList) {
			clazzes[i++] = clazz;
		}

		AgentMessage.sendInfo("is retrans form classes supported:", inst.isRetransformClassesSupported());
		// Class[] loadedClass = inst.getAllLoadedClasses();
		// for (Class clazz : loadedClass) {
		// AgentMessage.sendInfo("loadedClass:", clazz.getName());
		// }

		AgentTransformer rsa = new AgentTransformer();
		try {
			inst.addTransformer(rsa, true);
			inst.retransformClasses(clazzes);
		} catch (Exception e) {
			AgentMessage.sendError("Instrumentation fail:", e);
			finish(rsa, inst, clazzes);
			return;
		}

		// 启动通信线程
		startCommunicationThread(args);
		// 启动关闭线程
		startAgentEndThread(rsa, inst, clazzes);

		// 钩子
		Runtime.getRuntime().addShutdownHook(new Thread(new AgentEndThread(rsa, inst, clazzes, true), "agent-shutdown-reform-thread"));
		AgentMessage.sendInfo("agentmain end:");
	}

	private static void printClazzList(Set<Class> clazzList) {
		for (Class clazz : clazzList) {
			try {
				Field[] fieldsTmp = clazz.getFields();// 根据Class对象获得属性
				Set<Field> fields = new HashSet<Field>();
				fields.addAll(Arrays.asList(fieldsTmp));
				fieldsTmp = clazz.getDeclaredFields();
				fields.addAll(Arrays.asList(fieldsTmp));
				for (Field f : fields) {
					AgentMessage.sendInfo("field:", f.getName());// 打印每个属性的类型名字
				}

				Method[] methodsTmp = clazz.getMethods();
				Set<Method> methods = new HashSet<Method>();
				methods.addAll(Arrays.asList(methodsTmp));
				methodsTmp = clazz.getDeclaredMethods();
				methods.addAll(Arrays.asList(methodsTmp));
				for (Method m : methods) {
					AgentMessage.sendInfo("method:", m.getName());// 打印每个属性的类型名字
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// public static void main(String[] args) {
	// System.out.println(getClass("socket.Connector").getName());
	// }

	public static Set<Class> getClasses(String className) {
		Set<Class> result = new HashSet<Class>();
		for (int i = 0; i < 3; i++) {
			try {
				ThreadGroup group = Thread.currentThread().getThreadGroup();
				ThreadGroup topGroup = group;
				// 遍历线程组树，获取根线程组
				while (group != null) {
					topGroup = group;
					group = group.getParent();
				}
				// 激活的线程数再加8倍，防止枚举时有可能刚好有动态线程生成
				int slackSize = topGroup.activeCount() * 8;
				Thread[] slackThreads = new Thread[slackSize];
				// 获取根线程组下的所有线程，返回的actualSize便是最终的线程数
				int actualSize = topGroup.enumerate(slackThreads);
				Thread[] atualThreads = new Thread[actualSize];
				// 复制slackThreads中有效的值到atualThreads
				System.arraycopy(slackThreads, 0, atualThreads, 0, actualSize);
				for (Thread thread : atualThreads) {
					Class<?> clazz = null;
					try {
						if (null == thread.getContextClassLoader()) {
							continue;
						}
						clazz = thread.getContextClassLoader().loadClass(className);
					} catch (ClassNotFoundException e) {
						continue;
					}
					if (null != clazz) {
						result.add(clazz);
					}
				}
				Thread.sleep(100);
			} catch (Throwable t) {
				AgentMessage.sendError("find class exception:", t);
			}
		}
		return result;
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

	private static void startAgentEndThread(AgentTransformer rsa, Instrumentation inst, Class... clazz) {
		// reform 线程
		ExecutorService exec = Executors.newSingleThreadExecutor(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(Thread.currentThread().getThreadGroup(), r, "agent-reform-thread", 0);
			}
		});
		exec.execute(new AgentEndThread(rsa, inst, clazz));
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

		private Class[] clazz;

		private boolean force = false;

		public AgentEndThread(AgentTransformer rsa, Instrumentation inst, Class... clazz) {
			super();
			this.rsa = rsa;
			this.inst = inst;
			this.clazz = clazz;
		}

		public AgentEndThread(AgentTransformer rsa, Instrumentation inst, Class[] clazz, boolean force) {
			super();
			this.rsa = rsa;
			this.inst = inst;
			this.force = force;
			this.clazz = clazz;
		}

		@Override
		public void run() {
			try {
				if (!force) {
					ShutdownTrigger.shutdownWait();
				}
				finish(rsa, inst, clazz);
			} catch (Exception e) {
				AgentMessage.sendError("reform run exception:", e);
			}
		}
	}

	private static void finish(AgentTransformer rsa, Instrumentation inst, Class... clazz) {
		try {
			// 设置完成标志
			isFinished.set(true);
			// 关闭socket
			Connector.close();
			AgentMessage.sendInfo("agentmain reloading:");
			inst.removeTransformer(rsa);
			inst.retransformClasses(clazz);
		} catch (Exception e) {
			AgentMessage.sendError("reform finish exception:", e);
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
