package com.seaboat.thread;

public class InstructionReorderingDemo2 {

	static int a = 2;
	static boolean flg = false;

	public static void method1() {
		a = 1;
		flg = true;
	}

	public static void method2() {
		if (flg && a == 2) {
			System.out.println("a = " + a);
		}
	}

}
