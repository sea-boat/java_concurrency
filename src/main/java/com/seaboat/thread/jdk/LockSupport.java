package com.seaboat.thread.jdk;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class LockSupport {

	private static final Unsafe U;
	private static final long PARKBLOCKER;

	static {
		try {
			Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafeInstance.setAccessible(true);
			U = (Unsafe) theUnsafeInstance.get(Unsafe.class);
			PARKBLOCKER = U.objectFieldOffset(Thread.class.getDeclaredField("parkBlocker"));
		} catch (ReflectiveOperationException e) {
			throw new Error(e);
		}
	}

	private LockSupport() {
	}

	public static void park() {
		U.park(false, 0L);
	}

	public static void parkNanos(long nanos) {
		if (nanos > 0)
			U.park(false, nanos);
	}

	public static void parkUntil(long deadline) {
		U.park(true, deadline);
	}

	public static void park(Object blocker) {
		Thread t = Thread.currentThread();
		setBlocker(t, blocker);
		U.park(false, 0L);
		setBlocker(t, null);
	}

	public static void parkNanos(Object blocker, long nanos) {
		if (nanos > 0) {
			Thread t = Thread.currentThread();
			setBlocker(t, blocker);
			U.park(false, nanos);
			setBlocker(t, null);
		}
	}

	public static void parkUntil(Object blocker, long deadline) {
		Thread t = Thread.currentThread();
		setBlocker(t, blocker);
		U.park(true, deadline);
		setBlocker(t, null);
	}

	public static Object getBlocker(Thread t) {
		if (t == null)
			throw new NullPointerException();
		return U.getObjectVolatile(t, PARKBLOCKER);
	}

	private static void setBlocker(Thread t, Object arg) {
		U.putObject(t, PARKBLOCKER, arg);
	}

	public static void unpark(Thread thread) {
		if (thread != null)
			U.unpark(thread);
	}

}
