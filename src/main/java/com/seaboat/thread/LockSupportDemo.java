package com.seaboat.thread;

public class LockSupportDemo {
	private static String message;

	public static void main(String[] args) {
		Object lock = new Object();
		Thread thread1 = new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
				}
			}
			System.out.println(message);
		});
		Thread thread2 = new Thread(() -> {
			synchronized (lock) {
				message = "thread2 wakes up thread1";
				lock.notify();
			}
		});

		thread1.start();
		thread2.start();

	}
}
