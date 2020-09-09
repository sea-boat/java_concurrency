package com.seaboat.thread;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class MCSLock {
	private static Unsafe unsafe = null;
	volatile MCSNode tail;
	private static final long valueOffset;

	public static class MCSNode {
		MCSNode next;
		volatile boolean spin = true;
	}

	static {
		try {
			unsafe = getUnsafeInstance();
			valueOffset = unsafe.objectFieldOffset(MCSLock.class.getDeclaredField("tail"));
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	private static Unsafe getUnsafeInstance() throws Exception {
		Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
		theUnsafeInstance.setAccessible(true);
		return (Unsafe) theUnsafeInstance.get(Unsafe.class);
	}

	public void lock(MCSNode currentThreadMcsNode) {
		MCSNode predecessor = null;
		for (;;) {
			predecessor = tail;
			if (unsafe.compareAndSwapObject(this, valueOffset, tail, currentThreadMcsNode))
				break;
		}
		if (predecessor != null) {
			predecessor.next = currentThreadMcsNode;
			while (currentThreadMcsNode.spin) {
			}
		}
	}

	public void unlock(MCSNode currentThreadMcsNode) {
		if (tail != currentThreadMcsNode) {
			if (currentThreadMcsNode.next == null) {
				if (unsafe.compareAndSwapObject(this, valueOffset, currentThreadMcsNode, null)) {
					return;
				} else {
					while (currentThreadMcsNode.next == null) {
					}
				}
			}
			currentThreadMcsNode.next.spin = false;
		}
	}
}
