package com.seaboat.thread;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeInstanceTest {

	//实例化方式一
	public static Unsafe getUnsafeInstance_1() {
		return Unsafe.getUnsafe();
	}

	//实例化方式二
	public static Unsafe getUnsafeInstance_2() {
		try {
			Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafeField.setAccessible(true);
			return (Unsafe) theUnsafeField.get(null);
		} catch (Exception e) {
			return null;
		}
	}

	public static void main(String[] args) {
		Unsafe unsafe = UnsafeInstanceTest.getUnsafeInstance_1();//失败
		Unsafe unsafe2 = UnsafeInstanceTest.getUnsafeInstance_2();//成功
	}

}
