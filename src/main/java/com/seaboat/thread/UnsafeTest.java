package com.seaboat.thread;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeTest {

	private int flag = 100;
	private static long offset;
	private static Unsafe unsafe = null;

	static {
		try {
			Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafeField.setAccessible(true);
			unsafe = (Unsafe) theUnsafeField.get(null);
			offset = unsafe.objectFieldOffset(UnsafeTest.class.getDeclaredField("flag"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		int expect = 100;
		int update = 101;
		UnsafeTest unsafeTest = new UnsafeTest();
		System.out.println("flag字段的地址偏移为：" + offset);
		unsafe.compareAndSwapInt(unsafeTest, offset, expect, update);
		System.out.println("CAS操作后flagֵ的值为：" + unsafeTest.flag);
	}

}
