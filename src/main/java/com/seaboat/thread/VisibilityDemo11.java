package com.seaboat.thread;

public class VisibilityDemo11 {

	static int x = 0;

	public static void main(String[] args) {
		x = 3;
		Thread thread1 = new Thread(() -> {
			x = x * 2;
			Thread thread2 = new Thread(() -> {
				x = x * 2;
				System.out.println("thread2 x = " + x);
			});
			thread2.start();
		});
		thread1.start();
	}

}