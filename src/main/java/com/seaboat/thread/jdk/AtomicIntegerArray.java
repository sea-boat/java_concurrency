package com.seaboat.thread.jdk;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class AtomicIntegerArray {
	private static final VarHandle AA = MethodHandles.arrayElementVarHandle(int[].class);
	private final int[] array;

	public AtomicIntegerArray(int length) {
		array = new int[length];
	}

	public AtomicIntegerArray(int[] array) {
		this.array = array.clone();
	}

	public final int get(int i) {
		return (int) AA.getVolatile(array, i);
	}

	public final boolean compareAndSet(int i, int expectedValue, int newValue) {
		return AA.compareAndSet(array, i, expectedValue, newValue);
	}

	public final int incrementAndGet(int i) {
		return (int) AA.getAndAdd(array, i, 1) + 1;
	}

	public final int decrementAndGet(int i) {
		return (int) AA.getAndAdd(array, i, -1) - 1;
	}

}
