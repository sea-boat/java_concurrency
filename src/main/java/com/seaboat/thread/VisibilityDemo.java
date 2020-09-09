package com.seaboat.thread;

public class VisibilityDemo {
	static int x = 0;
	static int y = 1;

	public static void main(String[] args) throws InterruptedException {
		Thread thread1 = new Thread(() -> {
			while (true)
				if (x == 2 && y == 3) {
					System.out.println("thread1可见变量改变");
				}
		});
		Thread thread2 = new Thread(() -> {
			x = 2;
			y = 3;
		});
		thread1.start();
		Thread.sleep(1000);
		thread2.start();
	}
}