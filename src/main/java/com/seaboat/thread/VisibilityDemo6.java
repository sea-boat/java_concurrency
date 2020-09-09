package com.seaboat.thread;

public class VisibilityDemo6 {

	static volatile int x = 0;
	static int y = 0;

	public static void main(String[] args) throws InterruptedException {
		Thread thread1 = new Thread(() -> {
			y = 2;
			x = x + 4;
		});
		Thread thread2 = new Thread(() -> {
			System.out.println("thread2 x,y = " + x + "," + y);
		});
		thread1.start();
		Thread.sleep(1000);
		thread2.start();
	}
}