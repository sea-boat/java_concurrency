package com.seaboat.thread;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class SharedModel {
	private static Unsafe unsafe = null;
	private static final long stateOffset;
	private volatile int state = 10;
	static {
		try {
			unsafe = getUnsafeInstance();
			stateOffset = unsafe.objectFieldOffset(SharedModel.class.getDeclaredField("state"));
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	private static Unsafe getUnsafeInstance() throws Exception {
		Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
		theUnsafeInstance.setAccessible(true);
		return (Unsafe) theUnsafeInstance.get(Unsafe.class);
	}

	public int tryAcquireShared() {
		for (;;) {
			int newCount = state - 1;
			if (newCount >= 0
					&& unsafe.compareAndSwapInt(this, stateOffset, newCount + 1, newCount)) {
				return newCount;
			}
		}
	}

	public int tryReleaseShared() {
		for (;;) {
			int newCount = state + 1;
			if (unsafe.compareAndSwapInt(this, stateOffset, newCount - 1, newCount)) {
				return newCount;
			}
		}
	}
}

//public static void main(String[] args) {
//	SharedModel tl = new SharedModel();
//	for (int i = 0; i < 20; i++)
//		new Thread() {
//			public void run() {
//				System.out.println(tl.tryAcquireShared());
//				System.out.println(Thread.currentThread().getName());
//				System.out.println(tl.tryReleaseShared());
//			}
//		}.start();
//}
