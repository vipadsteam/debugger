/**
 * 
 */
package agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * @author USER
 *
 */
public class AgentTransformer implements ClassFileTransformer {

	public final String injectedClassName = "org.apache.commons.lang3.StringUtils";
	public final String methodName = "isBlank";

	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		System.out.println("transforming");
		className = className.replace("/", ".");
		// System.out.println(className);
		if (className.equals(injectedClassName)) {
			CtClass ctclass = null;
			try {
				ctclass = ClassPool.getDefault().get(className);// 使用全称,用于取得字节码类<使用javassist>
				// 解冻CtClass对象
				ctclass.defrost();
				CtMethod ctmethod = ctclass.getDeclaredMethod(methodName);// 得到这方法实例
				ctmethod.insertBefore("return true;");
				ctmethod.insertBefore("System.out.println(11111111);");
				ctmethod.insertBefore("System.out.println(22222222);");
				return ctclass.toBytecode();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}
}
