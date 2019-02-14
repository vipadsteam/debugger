package socket;

import java.nio.charset.Charset;

import bean.ClazzInfo;
import trigger.ShutdownTrigger;

public class AgentProtocol {
	
	private static final Charset UTF8 = Charset.forName("UTF-8");

	public static final String EOF = System.getProperty("line.separator");

	public static byte[] communicateToByte(boolean shutdownNow, int waitTime) {
		byte[] shutdownByte = new byte[] { booleanToByte(shutdownNow) };
		byte[] waitTimeByte = intToByteArray(waitTime);
		return getBytesFromObjs(shutdownByte, waitTimeByte, EOF.getBytes());
	}

	public static void byteToCommunicate(byte[] objBytes) throws ClassNotFoundException {
		byte[][] objsByte = getObjsFromBytes(objBytes, 2);
		if (byteToBoolean(objsByte[0][0])) {
			ShutdownTrigger.shutdownNow();
		}
		int waitTime = byteArrayToInt(objsByte[1]);
		ShutdownTrigger.setWaitTime(waitTime);
	}

	public static byte[] clazzInfoToByte() {
		Class clazz = ClazzInfo.getClazz();
		String name = clazz.getName();
		byte[] nameByte = name.getBytes(UTF8);
		byte[] clazzByte = ClazzInfo.getClazzByte();
		return getBytesFromObjs(nameByte, clazzByte, EOF.getBytes());
	}
	
	public static byte[] clazzInfoToByte(String name, byte[] clazzByte) {
		byte[] nameByte = name.getBytes(UTF8);
		return getBytesFromObjs(nameByte, clazzByte, EOF.getBytes());
	}

	public static void byteToClazzInfo(byte[] objBytes) throws ClassNotFoundException {
		byte[][] objsByte = getObjsFromBytes(objBytes, 2);
		String clazzName = new String(objsByte[0], UTF8);
		ClazzInfo.setClazz(Class.forName(clazzName));
		ClazzInfo.setClazzByte(objsByte[1]);
	}

	private static byte[] getBytesFromObjs(byte[]... objBytes) {
		int length = objBytes.length * 4;
		for (byte[] objByte : objBytes) {
			length += objByte.length;
		}
		byte[] result = new byte[length];
		int i = 0;
		for (byte[] objByte : objBytes) {
			int objByteLen = objByte.length;
			result[i++] = (byte) (objByteLen >> 24);
			result[i++] = (byte) ((byte) (objByteLen >> 16) & 0xff);
			result[i++] = (byte) ((byte) (objByteLen >> 8) & 0xff);
			result[i++] = (byte) (objByteLen & 0xff);
			System.arraycopy(objByte, 0, result, i, objByteLen);
			i += objByteLen;
		}
		return result;
	}

	private static byte[][] getObjsFromBytes(byte[] objBytes, int length) {
		byte[][] result = new byte[length][];
		int index = 0;
		for (int i = 0; i < length; i++) {
			byte[] objLenBytes = new byte[4];
			System.arraycopy(objBytes, index, objLenBytes, 0, 4);
			int objLen = byteArrayToInt(objLenBytes);
			index += 4;
			byte[] objBytesTmp = new byte[objLen];
			System.arraycopy(objBytes, index, objBytesTmp, 0, objLen);
			result[i] = objBytesTmp;
			index += objLen;
		}
		return result;
	}

	public static int byteArrayToInt(byte[] b) {
		return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
	}

	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) (i >> 24);
		result[1] = (byte) ((byte) (i >> 16) & 0xff);
		result[2] = (byte) ((byte) (i >> 8) & 0xff);
		result[3] = (byte) (i & 0xff);
		return result;
	}

	public static byte booleanToByte(boolean b) {
		if (b) {
			return 0x01;
		} else {
			return 0x00;
		}
	}

	public static boolean byteToBoolean(byte b) {
		return b == 0x01;
	}

	public static void main(String[] args) throws ClassNotFoundException {
		ClazzInfo.setClazz(agent.AgentMain.class);
		ClazzInfo.setClazzByte(new byte[] { (byte) 1, (byte) 2, (byte) 3 });
		byte[] bt = clazzInfoToByte();
		ClazzInfo.setClazz(null);
		ClazzInfo.setClazzByte(null);
		byteToClazzInfo(bt);
		System.out.println(ClazzInfo.getClazz().getName());
		for (byte b : ClazzInfo.getClazzByte()) {
			System.out.println((int) b);
		}
		byte[] com = communicateToByte(false, 100);
		byteToCommunicate(com);
		System.out.println(ShutdownTrigger.getWaitTime());
	}

}
