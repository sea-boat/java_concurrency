package com.seaboat.thread;

import com.seaboat.thread.jdk.LockSupport;

public class LockSupportDemo5 {

	private static String message;

	public static void main(String[] args) throws InterruptedException {
		Object lock = new Object();
		Thread thread1 = new Thread(() -> {
			synchronized (lock) {
				LockSupport.park();
				System.out.println(message);
			}
		});

		Thread thread2 = new Thread(() -> {
			synchronized (lock) {
				message = "thread2 wakes up thread1";
				LockSupport.unpark(thread1);
			}
		});

		thread1.start();
		Thread.sleep(3000);
		thread2.start();
	}
}
