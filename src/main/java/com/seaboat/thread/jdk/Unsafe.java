package com.seaboat.thread.jdk;

public final class Unsafe {

	private static native void registerNatives();

	static {
		registerNatives();
	}

	private Unsafe() {
	}

	private static final Unsafe theUnsafe = new Unsafe();

	public static Unsafe getUnsafe() {
		Class<?> caller = Reflection.getCallerClass();
		if (!VM.isSystemDomainLoader(caller.getClassLoader()))
			throw new SecurityException("Unsafe");
		return theUnsafe;
	}

	public final native boolean compareAndSwapInt(Object o, long offset, int expected, int x);

	public final native boolean compareAndSwapLong(Object o, long offset, long expected, long x);

	public final native boolean compareAndSwapObject(Object o, long offset, Object expected,
			Object x);

	public native void park(boolean isAbsolute, long time);

	public native void unpark(Object thread);

}
