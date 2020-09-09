package com.seaboat.thread;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class SpinLock {
	private static Unsafe unsafe = null;
	private static final long valueOffset;
	private volatile int value = 0;
	static {
		try {
			unsafe = getUnsafeInstance();
			valueOffset = unsafe.objectFieldOffset(SpinLock.class.getDeclaredField("value"));
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	private static Unsafe getUnsafeInstance() throws Exception {
		Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
		theUnsafeInstance.setAccessible(true);
		return (Unsafe) theUnsafeInstance.get(Unsafe.class);
	}

	public void lock() {
		for (;;) {
			int newV = value + 1;
			if (newV == 1)
				if (unsafe.compareAndSwapInt(this, valueOffset, 0, newV)) {
					return;
				}
		}
	}

	public void unlock() {
		unsafe.compareAndSwapInt(this, valueOffset, 1, 0);
	}
}
