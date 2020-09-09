package com.seaboat.thread;

public class WaitNotifyDemo {
	private static String message;

	public static void main(String[] args) {
		Object lock = new Object();
		Thread thread1 = new Thread(() -> {
			synchronized (lock) {
				if (message == null)
					try {
						lock.wait();
					} catch (InterruptedException e) {
					}
			}
			System.out.println(message);
		});

		Thread thread2 = new Thread(() -> {
			synchronized (lock) {
				message = "i am thread2";
				lock.notify();
			}
		});

		thread1.start();
		thread2.start();
	}
}
