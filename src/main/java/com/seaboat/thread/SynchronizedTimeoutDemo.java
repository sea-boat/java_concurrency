package com.seaboat.thread;

public class SynchronizedTimeoutDemo {
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
		Thread thread3 = new Thread(() -> {
			synchronized (lock) {
				System.out.println("Thread3 can never get the lock");
			}
		});
		try {
			thread1.start();
			Thread.sleep(1000);
			thread2.start();
			thread3.start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
