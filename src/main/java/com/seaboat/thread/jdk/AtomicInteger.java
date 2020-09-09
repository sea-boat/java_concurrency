package com.seaboat.thread.jdk;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class AtomicInteger {

	private static final Unsafe U;
	private static final long valueoffset;
	private volatile int value;

	static {
		try {
			Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafeInstance.setAccessible(true);
			U = (Unsafe) theUnsafeInstance.get(Unsafe.class);
			valueoffset = U.objectFieldOffset(AtomicInteger.class.getDeclaredField("value"));
		} catch (ReflectiveOperationException e) {
			throw new Error(e);
		}
	}

	public AtomicInteger() {
	}

	public AtomicInteger(int initialValue) {
		value = initialValue;
	}

	public final int get() {
		return value;
	}

	public final int getAndSet(int newValue) {
		int v;
		do {
			v = U.getIntVolatile(this, valueoffset);
		} while (!U.compareAndSwapInt(this, valueoffset, v, newValue));
		return v;
	}

	public final int getAndAdd(int delta) {
		int v;
		do {
			v = U.getIntVolatile(this, valueoffset);
		} while (!U.compareAndSwapInt(this, valueoffset, v, v + delta));
		return v;
	}

}
