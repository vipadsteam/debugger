/**
 * 
 */
package trigger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author USER
 *
 */
public class ShutdownTrigger {

	private static final CountDownLatch latch = new CountDownLatch(1);

	private static int waitTime = 0;

	public static void shutdownNow() {
		latch.countDown();
	}

	public static void shutdownWait() throws InterruptedException {
		if (0 == waitTime) {
			latch.await();
		} else {
			latch.await(waitTime, TimeUnit.SECONDS);
		}
	}

	/**
	 * @return the waitTime
	 */
	public static int getWaitTime() {
		return waitTime;
	}

	/**
	 * @param waitTime the waitTime to set
	 */
	public static void setWaitTime(int waitTime) {
		ShutdownTrigger.waitTime = waitTime;
	}
}
