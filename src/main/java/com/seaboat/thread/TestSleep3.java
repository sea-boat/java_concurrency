package com.seaboat.thread;

public class TestSleep3 {
	public static void main(String[] args) throws InterruptedException {
		Object lock = new Object();
		Thread thread1 = new Thread(() -> {
			synchronized (lock) {
				System.out.println("thread1 gets the lock.");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
				System.out.println("thread1 releases the lock.");
			}
		});
		Thread thread2 = new Thread(() -> {
			synchronized (lock) {
				System.out.println("thread2 gets the lock 3 second later.");
			}
		});
		thread1.start();
		Thread.sleep(100);
		thread2.start();
	}
}
