package com.seaboat.thread;

public class VisibilityDemo5 {
	static int x = 0;
	static Object lock = new Object();

	public static void main(String[] args) throws InterruptedException {
		Thread thread1 = new Thread(() -> {
			synchronized (lock) {
				x = x + 3;
				System.out.println("thread1 x = " + x);
			}
		});
		Thread thread2 = new Thread(() -> {
			synchronized (lock) {
				x = x + 4;
				System.out.println("thread2 x = " + x);
			}
		});
		thread1.start();
		thread2.start();
	}
}