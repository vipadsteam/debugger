/**
 * 
 */
package bean;

/**
 * @author USER
 *
 */
public class ClazzInfo {

	private static volatile Class clazz;

	private static volatile byte[] clazzByte;

	/**
	 * @return the clazz
	 */
	public static Class getClazz() {
		return clazz;
	}

	/**
	 * @param clazz
	 *            the clazz to set
	 */
	public static void setClazz(Class clazz) {
		ClazzInfo.clazz = clazz;
	}

	/**
	 * @return the clazzByte
	 */
	public static byte[] getClazzByte() {
		return clazzByte;
	}

	/**
	 * @param clazzByte
	 *            the clazzByte to set
	 */
	public static void setClazzByte(byte[] clazzByte) {
		ClazzInfo.clazzByte = clazzByte;
	}

}
