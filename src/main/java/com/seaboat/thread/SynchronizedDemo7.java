package com.seaboat.thread;

public class SynchronizedDemo7 {

	public static void method(String name) {
		synchronized (SynchronizedDemo7.class) {
			System.out.println(name + " gets the lock.");
			sleep(3000);
			System.out.println(name + " releases the lock after 3s.");
		}
	}

	public static synchronized void method2(String name) {
		System.out.println(name + " gets the lock.");
		sleep(3000);
		System.out.println(name + " releases the lock after 3s.");
	}

	public static void sleep(int s) {
		try {
			Thread.sleep(s);
		} catch (InterruptedException e) {
		}
	}

	public static void main(String[] args) throws InterruptedException {
		new Thread(() -> {
			method("thread1");
		}).start();

		new Thread(() -> {
			method2("thread2");
		}).start();

	}
}
