/**
 * 
 */
package agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import beans.ClazzInfo;
import message.AgentMessage;

/**
 * @author USER
 *
 */
public class AgentTransformer implements ClassFileTransformer {

	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		AgentMessage.sendInfo("transforming");
		className = className.replace("/", ".");
		String injectedClassName = ClazzInfo.getClazz();
		if (className.equals(injectedClassName)) {
			byte[] clazzByte = ClazzInfo.getClazzByte();
			if (null == clazzByte || clazzByte.length == 0) {
				return null;
			}
			return clazzByte;
		}
		return null;
	}
}
