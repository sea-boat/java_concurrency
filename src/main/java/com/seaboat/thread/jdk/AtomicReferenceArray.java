package com.seaboat.thread.jdk;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

public class AtomicReferenceArray<E> {
	private static final VarHandle AA = MethodHandles.arrayElementVarHandle(Object[].class);
	private final Object[] array;

	public AtomicReferenceArray(int length) {
		array = new Object[length];
	}

	public AtomicReferenceArray(E[] array) {
		this.array = Arrays.copyOf(array, array.length, Object[].class);
	}

	public final E get(int i) {
		return (E) AA.getVolatile(array, i);
	}

	public final boolean compareAndSet(int i, E expectedValue, E newValue) {
		return AA.compareAndSet(array, i, expectedValue, newValue);
	}

}
