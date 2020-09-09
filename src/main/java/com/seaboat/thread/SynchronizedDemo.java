package com.seaboat.thread;

public class SynchronizedDemo {

	public static void main(String[] args) {

		Object lock = new Object();
		Thread thread1 = new Thread(() -> {
			synchronized (lock) {
				System.out.println("Thread1 gets the lock");
				try {
					Thread.sleep(100000000000000000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		Thread thread2 = new Thread(() -> {
			synchronized (lock) {
				System.out.println("Thread2 can never get the lock");
			}
		});

		thread1.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		thread2.start();
	}
}
