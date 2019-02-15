/**
 * 
 */
package beans;

/**
 * @author USER
 *
 */
public class ClazzInfo {

	private static String clazz;

	private static byte[] clazzByte;

	/**
	 * @return the clazz
	 */
	public static String getClazz() {
		return clazz;
	}

	/**
	 * @param clazz
	 *            the clazz to set
	 */
	public static void setClazz(String clazz) {
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
