/**
 * 
 */
package trigger;

import java.util.concurrent.CountDownLatch;

/**
 * @author USER
 *
 */
public class ShutdownTrigger {

	private static final CountDownLatch latch = new CountDownLatch(1);

	public static void shutdownNow() {
		latch.countDown();
	}

	public static void shutdownWait() throws InterruptedException {
		latch.await();
	}
}
