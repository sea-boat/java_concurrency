package com.seaboat.thread;

public class SynchronizedDemo3 {

	public synchronized void method(String name) {
		System.out.println(name + " gets the lock.");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		System.out.println(name + " releases the lock after 3s.");
	}

	public static void main(String[] args) throws InterruptedException {
		SynchronizedDemo3 demo = new SynchronizedDemo3();

		new Thread(() -> {
			demo.method("thread1");
		}).start();

		new Thread(() -> {
			demo.method("thread2");
		}).start();

	}
}
