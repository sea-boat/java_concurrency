package com.seaboat.thread;

public class VisibilityDemo7 {

	static int x = 0;

	public static void main(String[] args) {
		x = 3;
		Thread thread1 = new Thread(() -> {
			x = x * 2;
			System.out.println("thread1 x = " + x);
		});
		thread1.start();
	}

}