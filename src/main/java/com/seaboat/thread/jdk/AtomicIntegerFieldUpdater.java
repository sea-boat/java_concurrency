package com.seaboat.thread.jdk;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import sun.misc.Unsafe;

public abstract class AtomicIntegerFieldUpdater<T> {
	private static final Unsafe U;

	static {
		try {
			Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafeInstance.setAccessible(true);
			U = (Unsafe) theUnsafeInstance.get(Unsafe.class);
		} catch (ReflectiveOperationException e) {
			throw new Error(e);
		}
	}

	public static <U> AtomicIntegerFieldUpdater<U> newUpdater(Class<U> tclass, String fieldName) {
		return new AtomicIntegerFieldUpdaterImpl<U>(tclass, fieldName);
	}

	public abstract boolean compareAndSet(T obj, int expect, int update);

	public abstract int get(T obj);

	public int incrementAndGet(T obj) {
		int prev, next;
		do {
			prev = get(obj);
			next = prev + 1;
		} while (!compareAndSet(obj, prev, next));
		return next;
	}

	private static final class AtomicIntegerFieldUpdaterImpl<T>
			extends AtomicIntegerFieldUpdater<T> {
		private final long offset;
		final int modifiers;

		AtomicIntegerFieldUpdaterImpl(final Class<T> tclass, final String fieldName) {
			final Field field;
			try {
				field = AccessController.doPrivileged(new PrivilegedExceptionAction<Field>() {
					public Field run() throws NoSuchFieldException {
						return tclass.getDeclaredField(fieldName);
					}
				});
				modifiers = field.getModifiers();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			if (!Modifier.isVolatile(modifiers))
				throw new IllegalArgumentException("Must be volatile type");
			this.offset = U.objectFieldOffset(field);
		}

		public final boolean compareAndSet(T obj, int expect, int update) {
			return U.compareAndSwapInt(obj, offset, expect, update);
		}

		public final int get(T obj) {
			return U.getIntVolatile(obj, offset);
		}

	}
}
