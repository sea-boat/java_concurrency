package com.seaboat.thread.jdk;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import sun.misc.Unsafe;

public abstract class AtomicReferenceFieldUpdater<T, V> {

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

	public static <U, W> AtomicReferenceFieldUpdater<U, W> newUpdater(Class<U> tclass,
			Class<W> vclass, String fieldName) {
		return new AtomicReferenceFieldUpdaterImpl<U, W>(tclass, vclass, fieldName);
	}

	public abstract boolean compareAndSet(T obj, V expect, V update);

	public abstract V get(T obj);

	private static final class AtomicReferenceFieldUpdaterImpl<T, V>
			extends AtomicReferenceFieldUpdater<T, V> {

		private final long offset;

		AtomicReferenceFieldUpdaterImpl(final Class<T> tclass, final Class<V> vclass,
				final String fieldName) {
			final Field field;
			final int modifiers;
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

		public final boolean compareAndSet(T obj, V expect, V update) {
			return U.compareAndSwapObject(obj, offset, expect, update);
		}

		public final V get(T obj) {
			return (V) U.getObjectVolatile(obj, offset);
		}
	}
}
