package com.seaboat.thread;

public class SynchronizedInterruptDemo {

	public static void main(String[] args) {
		Object lock = new Object();

		Thread thread1 = new Thread(() -> {
			synchronized (lock) {
				System.out.println("Thread1 gets the lock");
				try {
					Thread.sleep(100000000000000000L);
				} catch (InterruptedException e) {
				}
			}
		});

		Thread thread2 = new Thread(() -> {
			synchronized (lock) {
				System.out.println("Thread2 can never get the lock");
			}
		});

		try {
			thread1.start();
			Thread.sleep(1000);
			thread2.start();
			Thread.sleep(2000);
			thread2.interrupt();
			System.out.println("Thread2 can't be interrupted");
		} catch (InterruptedException e) {
		}
	}
}
