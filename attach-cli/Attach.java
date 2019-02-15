
/**
 * 
 */

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.sun.tools.attach.VirtualMachine;

/**
 * @author USER
 *
 */
public class Attach {

	public static void main(String[] args) {
		try {
			VirtualMachine vm = null;
			File directory = new File("");
			String attachMainPath = directory.getAbsolutePath();
			String agentjarpath = attachMainPath + "/JavaAgentAttachTest.jar"; // agentjar路径
			String pid = Files.readAllLines(Paths.get("pid.log")).get(0);// 目标JVM的进程ID（PID）
			String url = Files.readAllLines(Paths.get("url.log")).get(0);// ip:port
			System.out.println("pid:" + pid);
			vm = VirtualMachine.attach(pid);
			System.out.println("agentjarpath:" + agentjarpath + " url:" + url);
			vm.loadAgent(agentjarpath, url);
			vm.detach();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
