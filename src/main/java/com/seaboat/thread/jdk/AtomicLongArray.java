package com.seaboat.thread.jdk;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class AtomicLongArray {
	private static final VarHandle AA = MethodHandles.arrayElementVarHandle(long[].class);
	private final long[] array;

	public AtomicLongArray(int length) {
		array = new long[length];
	}

	public AtomicLongArray(long[] array) {
		this.array = array.clone();
	}

	public final long get(int i) {
		return (long) AA.getVolatile(array, i);
	}

	public final boolean compareAndSet(int i, long expectedValue, long newValue) {
		return AA.compareAndSet(array, i, expectedValue, newValue);
	}

	public final long incrementAndGet(int i) {
		return (long) AA.getAndAdd(array, i, 1L) + 1L;
	}

	public final long decrementAndGet(int i) {
		return (long) AA.getAndAdd(array, i, -1L) - 1L;
	}

}
