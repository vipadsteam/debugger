
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

	public static void main(String[] args) throws Exception {
		VirtualMachine vm = null;
		String agentjarpath = "JavaAgentAttachTest.jar"; // agentjar路径
		String content = Files.readAllLines(Paths.get("pid.log")).get(0);// 目标JVM的进程ID（PID）
		String url = Files.readAllLines(Paths.get("url.log")).get(0);// ip:port
		vm = VirtualMachine.attach(content);
		System.out.println("agentjarpath:" + agentjarpath + " url:" + url);
		vm.loadAgent(agentjarpath, url);
		vm.detach();
	}
}
