package com.seaboat.thread;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class ExclusiveModel {
	private static Unsafe unsafe = null;
	private static final long stateOffset;
	private volatile int state = 0;
	static {
		try {
			unsafe = getUnsafeInstance();
			stateOffset = unsafe.objectFieldOffset(ExclusiveModel.class.getDeclaredField("state"));
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	private static Unsafe getUnsafeInstance() throws Exception {
		Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
		theUnsafeInstance.setAccessible(true);
		return (Unsafe) theUnsafeInstance.get(Unsafe.class);
	}

	public void tryAcquire() {
		for (;;) {
			int newV = state + 1;
			if (newV == 1)
				if (unsafe.compareAndSwapInt(this, stateOffset, 0, newV)) {
					return;
				}
		}
	}

	public void tryRelease() {
		unsafe.compareAndSwapInt(this, stateOffset, 1, 0);
	}

}

//public static void main(String[] args) {
//	ExclusiveModel tl = new ExclusiveModel();
//	for (int i = 0; i < 20; i++)
//		new Thread() {
//			public void run() {
//				tl.tryAcquire();
//				System.out.println(Thread.currentThread().getName());
//				tl.tryRelease();
//			}
//		}.start();
//}
