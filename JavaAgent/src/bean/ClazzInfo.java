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

}
